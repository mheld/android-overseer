package com.overseer.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.overseer.db.DatabaseAdapter;
import com.overseer.db.DatabaseAdapter.ActivityPointColumns;
import com.overseer.utils.ActivityVector;

import android.database.Cursor;

public class ActivityPoint implements DatabaseElement<ActivityPoint> {
	double magnitude;
	Date created_at;
	
	public ActivityPoint(double m){
		this.magnitude = m;
	}
	
	public ActivityPoint(ArrayList<ActivityVector> vectors){
		this.magnitude = ActivityVector.averageVectors(vectors);
	}
	
	private ActivityPoint(double m, Date t){
		this(m);
		this.created_at = t;
	}
	
	public ActivityPoint(Cursor c){
		this(c.getDouble(c.getColumnIndex(ActivityPointColumns.MAGNITUDE)),
				new Date(c.getLong(c.getColumnIndex(ActivityPointColumns.CREATED_AT))));
	}

	public double getMagnitude() {
		return this.magnitude;
	}

	public void setLatitude(double m) {
		this.magnitude = m;
	}

	public Date getCreatedAt() {
		return created_at;
	}

	@Override
	public List<ActivityPoint> create(DatabaseAdapter db) {
		db.create(this);
		
		return db.getActivityPoints();
	}
	
	public static List<ActivityPoint> allActivityPoints(DatabaseAdapter db){
		return db.getActivityPoints();
	}

}
