package com.overseer.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.overseer.db.DatabaseAdapter;
import com.overseer.db.DatabaseAdapter.CoordinateColumns;

import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

public class Coordinate implements DatabaseElement<Coordinate> {
	private double latitude;
	private double longitude;
	private Date created_at;
	
	
	private Coordinate(){}
	
	public Coordinate(Location loc) {
		this(loc.getLatitude(), loc.getLongitude());
	}
	
	private Coordinate(double lat, double lon){
		this.latitude = lat;
		this.longitude = lon;
	}
	
	private Coordinate(double lat, double lon, Date t){
		this(lat,lon);
		this.created_at = t;
	}
	
	public Coordinate(Cursor c){
		this(c.getDouble(c.getColumnIndex(CoordinateColumns.LATITUDE)),
				c.getDouble(c.getColumnIndex(CoordinateColumns.LONGITUDE)),
				new Date(c.getLong(c.getColumnIndex(CoordinateColumns.CREATED_AT))));
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public Date getCreatedAt() {
		return created_at;
	}
	
	public GeoPoint toGeoPoint(){
		return new GeoPoint(
				(int) (this.latitude * 1e6),
				(int) (this.longitude * 1e6));
	}

	@Override
	public List<Coordinate> create(DatabaseAdapter db) {
		db.create(this);
		
		return db.getCoordinates();
	}
	
	public static List<Coordinate> allBetween(DatabaseAdapter db, Date left, Date right){
		return db.getCoordinatesBetween(left, right);
	}
	
	public static List<Coordinate> all(DatabaseAdapter db){
		if("google_sdk".equals( Build.PRODUCT )){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, -2);
			List<Coordinate> coordinates = new ArrayList<Coordinate>();
			Double[] positions = new Double[]{
					25.11937, 55.139308,   /**/ 25.11938, 55.139308,   /**/ 25.11989, 55.139308,   /**/ 25.11907, 55.139308,   /**/ 25.11922, 55.139308,   /**/ 25.11921, 55.139308,   /**/
					42.339387, -71.087798, /**/ 42.339487, -71.087798, /**/ 42.339357, -71.087798, /**/ 42.339367, -71.087798, /**/ 42.339307, -71.087798, /**/ 42.339000, -71.087798, /**/
					25.11937, 55.139308,   /**/ 25.11938, 55.139308,   /**/ 25.11989, 55.139308,   /**/ 28.11907, 55.139308,   /**/ 28.11922, 55.139308,   /**/ 28.11921, 55.139308,   /**/
					42.339387, -71.087798, /**/ 42.339487, -71.087798, /**/ 42.339357, -71.087798, /**/ 48.339367, -71.087798, /**/ 48.339307, -71.087798, /**/ 48.339000, -71.087798, /**/
			};
			for(int i = 0; i<positions.length-2; i+=2){
				coordinates.add(new Coordinate(positions[i], positions[i+1], cal.getTime()));
				cal.add(Calendar.MINUTE, 5);
			}
			
			return coordinates;
		}else{
			return db.getCoordinates();
		}
	}
	
	public static Coordinate fromBundle(Bundle extras){
		Coordinate c = new Coordinate();
		c.setLatitude(extras.getDouble("latitude"));
		c.setLongitude(extras.getDouble("longitude"));
		return c;
	}

}
