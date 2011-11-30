package com.overseer.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.overseer.db.DatabaseAdapter;
import com.overseer.db.DatabaseAdapter.ActivityPointColumns;
import com.overseer.utils.ActivityVector;

import android.database.Cursor;
import android.os.Build;

public class ActivityPoint implements DatabaseElement<ActivityPoint> {
	private double magnitude;
	private Date created_at;
	
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
	
	public static List<ActivityPoint> allBetween(DatabaseAdapter db, Date left, Date right){
		return null;
	}
	
	public static List<ActivityPoint> all(DatabaseAdapter db){
		if("google_sdk".equals( Build.PRODUCT )){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, -2);
			List<ActivityPoint> points = new ArrayList<ActivityPoint>();
			Integer[] magnitudes = new Integer[]{
					10, 10, 10, 10, 11, 11, //roughly each line should cluster
					14, 15, 14, 14, 15, 15,
					10, 9,  10, 10,  9, 9,
					12, 12, 11, 12, 13, 13
					
			};
			for(int i = 0; i<magnitudes.length-1; i++){
				points.add(new ActivityPoint(magnitudes[i], cal.getTime()));
				cal.add(Calendar.MINUTE, 5);
			}
			
			
			return points;
		}else{
			return db.getActivityPoints();
		}
	}
}
