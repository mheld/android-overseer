package com.overseer.models;

import java.util.Date;
import java.util.List;

import com.overseer.db.DatabaseAdapter;
import com.overseer.db.DatabaseAdapter.CoordinateColumns;

import android.database.Cursor;
import android.location.Location;

public class Coordinate implements DatabaseElement<Coordinate> {
	Double latitude;
	Double longitude;
	Date created_at;
	
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

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public Date getCreatedAt() {
		return created_at;
	}

	@Override
	public List<Coordinate> create(DatabaseAdapter db) {
		db.create(this);
		
		//TODO: this should return a list of the coordinates that are around
		return null;
	}
	
	public static List<Coordinate> allCoordinates(DatabaseAdapter db){
		return null;
	}

}
