package com.overseer.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;

import com.overseer.db.DatabaseAdapter;
import com.overseer.db.DatabaseAdapter.ChunkColumns;

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
			setFrom(left);
			setUntil(right);
		}else{
			if(getFrom().before(left)){
				// already contains the period
			}else{
				setFrom(left);
			}
			
			if(getUntil().after(right)){
				// already contains the period
			}else{
				setUntil(right);
			}
		}
	}
	
	// calculates chunks by truncating data (stops at the two digits after the decimal)
	public static List<Chunk> calculateChunksByCoordinates(DatabaseAdapter db){
		List<Coordinate> coordinates = Coordinate.all(db);
		List<Chunk> ret = new ArrayList<Chunk>();
		Coordinate left;
		Coordinate right;
		Chunk temp = new Chunk();
		
		for(int i = 0; i<=coordinates.size()-2; i+=2){
			left = coordinates.get(i);
			right = coordinates.get(i+1);
			
			left.setLatitude(round(left.getLatitude(), 2));
			left.setLongitude(round(left.getLongitude(), 2));
			right.setLatitude(round(right.getLatitude(), 2));
			right.setLongitude(round(right.getLongitude(), 2));
			
			if(left.getLatitude() == right.getLatitude() ||
					left.getLongitude() == right.getLongitude()){
				// if the chunk is in range, expand the availability of the chunk
				temp.setAvailableFor(left.getCreatedAt(), right.getCreatedAt());
			}else{
				// otherwise, save the current chunk and start again!
				ret.add(temp);
				temp = new Chunk();
				temp.setAvailableFor(left.getCreatedAt(), right.getCreatedAt());
			}
			
		}
		return ret;
	}
	
	public static List<Chunk> calculateChunksByActivityPoints(DatabaseAdapter db){
		List<ActivityPoint> points = ActivityPoint.all(db);
		List<Chunk> ret = new ArrayList<Chunk>();
		ActivityPoint left;
		ActivityPoint right;
		Chunk temp = new Chunk();
		double avg;
		int MAXDIFFERENCE = 2;
		
		for(int i = 0; i<=points.size()-2; i+=2){
			left = points.get(i);
			right = points.get(i+1);
			
			avg = (left.getMagnitude() + right.getMagnitude())/2;
			
			if((avg-left.getMagnitude()) < MAXDIFFERENCE){
				// if the chunk is in range, expand the availability of the chunk
				temp.setAvailableFor(left.getCreatedAt(), right.getCreatedAt());
			}else{
				// otherwise, save the current chunk and start again!
				ret.add(temp);
				temp = new Chunk();
				temp.setAvailableFor(left.getCreatedAt(), right.getCreatedAt());
			}
		}
		return ret;
	}
	
	public static List<Chunk> all(DatabaseAdapter db){
		return db.getChunks();
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
