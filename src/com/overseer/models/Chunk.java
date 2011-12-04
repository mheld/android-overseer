package com.overseer.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;

import com.overseer.db.DatabaseAdapter;
import com.overseer.db.DatabaseAdapter.ChunkColumns;
import com.overseer.utils.Log;

public class Chunk {
	private Date from;
	private Date until;
	private Date created_at;
	private String activityCategory;
	private String food;
	private int stressLevel;
	
	private Chunk(){}
	
	public Chunk(Cursor c){
		this();
		setFrom(new Date(c.getLong(c.getColumnIndex(ChunkColumns.FROM))));
		setUntil(new Date(c.getLong(c.getColumnIndex(ChunkColumns.UNTIL))));
		setCreatedAt(new Date(c.getLong(c.getColumnIndex(ChunkColumns.CREATED_AT))));
		setActivityCategory(c.getString(c.getColumnIndex(ChunkColumns.ACTIVITY_CATEGORY)));
		setFood(c.getString(c.getColumnIndex(ChunkColumns.FOOD)));
		setStressLevel(c.getInt(c.getColumnIndex(ChunkColumns.STRESS)));
	}
	
	public Date getFrom() {
		return from;
	}
	
	public void setFrom(Date from) {
		this.from = from;
	}
	
	public Date getUntil() {
		return until;
	}
	
	public void setUntil(Date until) {
		this.until = until;
	}
	
	public Date getCreatedAt() {
		return created_at;
	}
	
	public void setCreatedAt(Date c) {
		this.created_at = c;
	}
	
	public String getActivityCategory() {
		return activityCategory;
	}
	
	public void setActivityCategory(String activityCategory) {
		this.activityCategory = activityCategory;
	}
	
	public boolean ateFood(){
		return this.getFood() != null && !this.getFood().equals("");
	}
	
	public String getFood() {
		return food;
	}
	
	public void setFood(String food) {
		this.food = food;
	}
	
	public int getStressLevel() {
		return stressLevel;
	}
	
	public void setStressLevel(int stressLevel) {
		this.stressLevel = stressLevel;
	}
	
	public void setAvailableFor(Date left, Date right){
		if(getFrom() == null){
			this.setFrom(left);
			this.setUntil(right);
		}else{
			if(this.getFrom().before(left)){
				// already contains the period
			}else{
				this.setFrom(left);
			}
			
			if(this.getUntil().after(right)){
				// already contains the period
			}else{
				this.setUntil(right);
			}
		}
	}
	
	// calculates chunks by truncating data (stops at the three digits after the decimal)
	public static List<Chunk> calculateChunksByCoordinates(DatabaseAdapter db){
		Log.d("I AM CHUNKING!");
		List<Coordinate> coordinates = Coordinate.all(db);
		List<Chunk> ret = new ArrayList<Chunk>();
		Coordinate left;
		Coordinate right;
		Chunk temp = new Chunk();
		
		for(int i = 0; i<=coordinates.size()-2; i++){
			left = coordinates.get(i);
			right = coordinates.get(i+1);
			
			left.setLatitude(round(left.getLatitude(), 3));
			left.setLongitude(round(left.getLongitude(), 3));
			right.setLatitude(round(right.getLatitude(), 3));
			right.setLongitude(round(right.getLongitude(), 3));
			
			Log.d(left.getLatitude() + ", " + left.getLongitude() + " == " + right.getLatitude() + ", "+ right.getLongitude());
			
			if(left.getLatitude() == right.getLatitude() ||
					left.getLongitude() == right.getLongitude()){
				// if the chunk is in range, expand the availability of the chunk
				temp.setAvailableFor(left.getCreatedAt(), right.getCreatedAt());
				Log.d("JUST EXPANDED THIS CHUNK -> " + temp.getFrom() + ", " + temp.getUntil());
			}else{
				// otherwise, save the current chunk and start again!
				ret.add(temp);
				Log.d("JUST ADDED THIS CHUNK -> " + temp.getFrom());
				temp = new Chunk();
				temp.setAvailableFor(left.getCreatedAt(), right.getCreatedAt());
			}
			
		}
		return ret;
	}
	
	//TODO: CHANGE THE MAXDIFFERENCE TO SOMETHING REASONABLE
	public static List<Chunk> calculateChunksByActivityPoints(DatabaseAdapter db){
		Log.d("I AM CHUNKING!");
		List<ActivityPoint> points = ActivityPoint.all(db);
		List<Chunk> ret = new ArrayList<Chunk>();
		ActivityPoint left;
		ActivityPoint right;
		Chunk temp = new Chunk();
		double avg;
		int MAXDIFFERENCE = 1;
		
		for(int i = 0; i<=points.size()-2; i++){
			left = points.get(i);
			right = points.get(i+1);
			
			avg = (left.getMagnitude() + right.getMagnitude())/2;
			
			Log.d("difference -> " + Math.abs(avg-left.getMagnitude()));
			
			if(Math.abs(avg-left.getMagnitude()) < MAXDIFFERENCE){
				// if the chunk is in range, expand the availability of the chunk
				temp.setAvailableFor(left.getCreatedAt(), right.getCreatedAt());
				Log.d("JUST EXPANDED THIS CHUNK -> " + temp.getFrom() + ", " + temp.getUntil());
			}else{
				// otherwise, save the current chunk and start again!
				ret.add(temp);
				Log.d("JUST ADDED THIS CHUNK -> " + temp.getFrom());
				temp = new Chunk();
				temp.setAvailableFor(left.getCreatedAt(), right.getCreatedAt());
			}
		}
		return ret;
	}
	
	public static List<Chunk> all(DatabaseAdapter db){
		return db.getChunks();
	}
	
	public static List<Coordinate> allBetween(DatabaseAdapter db, Date left, Date right){
		return db.getCoordinatesBetween(left, right);
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
