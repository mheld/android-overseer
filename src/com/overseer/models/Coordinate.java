package com.overseer.models;

import java.util.Date;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.overseer.db.DatabaseAdapter;
import com.overseer.db.DatabaseAdapter.CoordinateColumns;

import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;

public class Coordinate implements DatabaseElement<Coordinate> {
	double latitude;
	double longitude;
	Date created_at;
	
	
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
	
	public static List<Coordinate> allCoordinates(DatabaseAdapter db){
		return db.getCoordinates();
	}
	
	public static Coordinate fromBundle(Bundle extras){
		Coordinate c = new Coordinate();
		c.setLatitude(extras.getDouble("latitude"));
		c.setLongitude(extras.getDouble("longitude"));
		return c;
	}

}
