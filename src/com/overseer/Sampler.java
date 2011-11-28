package com.overseer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.overseer.MyLocation.LocationResult;
import com.overseer.db.DbDoer;
import com.overseer.models.ActivityPoint;
import com.overseer.models.Coordinate;
import com.overseer.utils.ActivityVector;
import com.overseer.utils.Log;


public class Sampler extends IntentService {
	public Sampler() {
		super("Sampler Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle b = intent.getExtras();
		Context context = getApplicationContext();

		String method = "";
		try {
			method = b.getString("method");
		} catch (Exception e) {
			method = "sampleGps";
		}

		try {
			if (method.equals("sampleGps")) {
				Log.d("I AM SAMPLING!");
				sampleGps(context);
				sampleActivity(context);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void sendMessage(Context context, String s){
		Intent i = new Intent(context, Sampler.class);
		i.putExtra("method", s);
		context.startService(i);
	}

	private void sampleActivity(Context context){
		final SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
		final ArrayList<ActivityVector> vectors = new ArrayList<ActivityVector>();
		
		SensorEventListener accelerationListener = new SensorEventListener() {
			private int count = 0;
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int acc) {
			}
	 
			@Override
			public void onSensorChanged(SensorEvent event) {
				count++;
				vectors.add(new ActivityVector(event.values[0], event.values[1], event.values[2]));
				if(count >= 60){
					sensorManager.unregisterListener(this);
				}
			}
	 
		};
		
		sensorManager.registerListener(accelerationListener, sensor, 1000);
		
		final ActivityPoint dp = new ActivityPoint(vectors);
		new DbDoer<Object>(context){

			@Override
			public Object perform() {
				dp.create(db);
				return null;
			}
			
		};
	}
	
	
	private void sampleGps(final Context context){
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
				MyLocation myLocation = new MyLocation();

				LocationResult locationResult = new LocationResult() {
					public void gotLocation(final Location loc) {
						try {
							final Coordinate coord = new Coordinate(loc);
							new DbDoer<Object>(context){

								@Override
								public Object perform() {
									coord.create(db);
									return null;
								}
								
							};
						} catch (Exception e) {
							// do nothing
						}
					};
				};

				myLocation.getLocation(context, locationResult);
			}else{
				//TODO: what happens when we're not connected?
			}
		} catch (Exception e) {
			//TODO: what happens when there's no network?
			e.printStackTrace();
		}
	}
}


/*
 * stolen from http://stackoverflow.com/questions/3145089/
 * what-is-the-simplest-and
 * -most-robust-way-to-get-the-users-current-location-in-an/3145655#3145655
 */
class MyLocation {
	Timer timer1;
	LocationManager lm;
	LocationResult locationResult;
	boolean gps_enabled = false;
	boolean network_enabled = false;

	public boolean getLocation(Context context, LocationResult result) {
		// I use LocationResult callback class to pass location value from
		// MyLocation to user code.
		locationResult = result;
		if (lm == null) {
			lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		}

		// exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		// don't start listeners if no provider is enabled
		if (!gps_enabled && !network_enabled) {
			return false;
		}

		if (gps_enabled) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
					locationListenerGps);
		}
		if (network_enabled) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
					locationListenerNetwork);
		}
		timer1 = new Timer();
		timer1.schedule(new GetLastLocation(), 20000);
		return true;
	}

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer1.cancel();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerNetwork);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer1.cancel();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerGps);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	class GetLastLocation extends TimerTask {
		public void run() {
			lm.removeUpdates(locationListenerGps);
			lm.removeUpdates(locationListenerNetwork);

			Location net_loc = null, gps_loc = null;
			if (gps_enabled) {
				gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			if (network_enabled) {
				net_loc = lm
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}

			// if there are both values use the latest one
			if (gps_loc != null && net_loc != null) {
				if (gps_loc.getTime() > net_loc.getTime()) {
					locationResult.gotLocation(gps_loc);
				} else {
					locationResult.gotLocation(net_loc);
				}
				return;
			}

			if (gps_loc != null) {
				locationResult.gotLocation(gps_loc);
				return;
			}
			if (net_loc != null) {
				locationResult.gotLocation(net_loc);
				return;
			}
			locationResult.gotLocation(null);
		}
	}

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}