package com.overseer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.overseer.db.DbDoer;
import com.overseer.models.ActivityPoint;
import com.overseer.models.Chunk;
import com.overseer.models.Coordinate;
import com.overseer.utils.BasicItemizedOverlay;
import com.overseer.utils.Comms;
import com.overseer.utils.Log;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;

public class Viewer extends MapActivity {
	static MapView mMapView;
	List<Coordinate> mCoordinates;
	List<ActivityPoint> mActivities;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ensureSampling();
        setupState();
        drawActivityGraph();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	drawMap(); 
    	//drawMap needs to be in onResume because we move the map into SingleEventViewer
    }
    
    private void ensureSampling(){
    	Intent i = new Intent(this, SampleScheduler.class);
    	this.sendBroadcast(i);
    }
    
    private void setupState(){
    	
    	Comms.getComms(this);
    	
    	new DbDoer<Object>(this){

			@Override
			public Object perform() {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.HOUR_OF_DAY, -1);
				Date left = cal.getTime();
				cal.add(Calendar.HOUR_OF_DAY, 1);
				Date right = cal.getTime();
				mCoordinates = Coordinate.all(db);
				//mActivities = ActivityPoint.all(db);
				for(Chunk c: Chunk.calculateChunksByCoordinates(db)){
				//for(Chunk c : Chunk.calculateChunksByActivityPoints(db)){
					Log.d("start at -> "+c.getFrom());
					Log.d("end at -> "+c.getUntil());
				}
				//ActivityPoint.allBetween(db, left, right);
				//Coordinate.allBetween(db, left, right);
				
				
				
				return null;
			}
        	
        };
    }
    
    private void drawMap(){
    	mMapView = Viewer.materializeMap(this);
    	LinearLayout mapLayout = (LinearLayout)findViewById(R.id.main_map_view);
    	mapLayout.addView(mMapView);
    	List<Overlay> mapOverlays = mMapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        
        BasicItemizedOverlay itemizedoverlay = new BasicItemizedOverlay(drawable);        
        for(Coordinate c : mCoordinates){
        	itemizedoverlay.addOverlay(new OverlayItem(c.toGeoPoint(), "", ""));
        }
        
        mapOverlays.add(itemizedoverlay);
    }
    
    private void drawActivityGraph(){
    	//TODO: fill in
    }
    
    //yay, singleton!
    public static MapView materializeMap(Context context){
    	if(mMapView == null){
    		mMapView = new MapView(context, context.getString(R.string.maps_key));
    	}else{
    		//we've already created it
    	}
    	
    	return mMapView;
    }


	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}