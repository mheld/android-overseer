package com.overseer.models;

import java.util.List;

import com.overseer.db.DatabaseAdapter;

import android.location.Location;

public class Coordinate implements DatabaseElement<Coordinate> {
	Double latitude;
	Double longitude;
	
	public Coordinate(Location loc) {
		this.latitude = loc.getLatitude();
		this.longitude = loc.getLongitude();
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

	@Override
	public List<Coordinate> create(DatabaseAdapter db) {
		
		return null;
	}

}
