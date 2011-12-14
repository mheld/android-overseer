package com.overseer.models;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.csvreader.CsvReader;
import com.overseer.db.DatabaseAdapter;
import com.overseer.db.DatabaseAdapter.ActivityPointColumns;
import com.overseer.utils.ActivityVector;

import android.content.Context;
import android.content.res.AssetManager;
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

	ActivityPoint(double m, Date t){
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
		return db.getActivityPointsBetween(left, right);
	}

	public static List<ActivityPoint> all(DatabaseAdapter db){
		if("google_sdk".equals( Build.PRODUCT )){
			return getFromSample(db.getContext());
		}else{
			return db.getActivityPoints();
		}
	}

	public static List<ActivityPoint> getFromSample(Context context){
		List<ActivityPoint> actPoints = new ArrayList<ActivityPoint>();
		AssetManager assets = context.getAssets();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY,-5); //not sure how long that data stream was

		try {
			ActivityPoint tempAct;
			CsvReader acts = new CsvReader(assets.open("Actigraphs.csv"), Charset.defaultCharset());

			while (acts.readRecord()){
				try{
					acts.get(0);
				}catch(IOException e){
					//we've hit a blank line
					break;
				}
				tempAct = new ActivityPoint(
						Math.sqrt(
								Math.pow(Double.parseDouble(acts.get(0)), 2) +
								Math.pow(Double.parseDouble(acts.get(1)), 2) +
								Math.pow(Double.parseDouble(acts.get(2)), 2) ));
				tempAct.created_at = cal.getTime();
				cal.add(Calendar.MILLISECOND, 5);

				actPoints.add(tempAct);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return actPoints;	
	}
}
