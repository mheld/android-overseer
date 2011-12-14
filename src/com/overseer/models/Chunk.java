package com.overseer.models;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.res.AssetManager;
import android.database.Cursor;

import com.csvreader.CsvReader;
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
	
	//TODO: CHANGE THE MAXDIFFERENCE TO SOMETHING REASONABLE
	public static List<Chunk> calculateChunksByActivityPoints(DatabaseAdapter db){
		Log.d("I AM CHUNKING!");
		double diff;
		double MAXDIFFERENCE = 0.2;	
		BigInteger i = BigInteger.ZERO;
		
		AssetManager assets = db.getContext().getAssets();
		List<Chunk> ret = new ArrayList<Chunk>();
		ActivityPoint left = null;
		ActivityPoint right;
		Chunk temp = new Chunk();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY,-5); //not sure how long that data stream was

		try {
			CsvReader acts = new CsvReader(assets.open("Actigraphs.csv"), Charset.defaultCharset());
			boolean first = true;
			while (acts.readRecord()){
				
				try{
					acts.get(0);
				}catch(IOException e){
					//we've hit a blank line
					break;
				}
				
				if(first){
					//set left
					left = new ActivityPoint(
							Math.sqrt(
									Math.pow(Double.parseDouble(acts.get(0)), 2) +
									Math.pow(Double.parseDouble(acts.get(1)), 2) +
									Math.pow(Double.parseDouble(acts.get(2)), 2) ), cal.getTime());
				}else{
					//set right
					right = new ActivityPoint(
							Math.sqrt(
									Math.pow(Double.parseDouble(acts.get(0)), 2) +
									Math.pow(Double.parseDouble(acts.get(1)), 2) +
									Math.pow(Double.parseDouble(acts.get(2)), 2) ), cal.getTime());
					//compare
					diff = Math.abs(left.getMagnitude() - right.getMagnitude());
					
					Log.d(i + " diff: " + diff + " -- tol: " + left.getMagnitude()*MAXDIFFERENCE);
					
					//if the difference between the chunks is less that 20% of the left chunk
					if(diff < left.getMagnitude()*MAXDIFFERENCE ){
						// if the chunk is in range, expand the availability of the chunk
						temp.setAvailableFor(left.getCreatedAt(), right.getCreatedAt());
						Log.d("JUST EXPANDED THIS CHUNK -> " + temp.getFrom() + ", " + temp.getUntil());
					}else{
						// otherwise, save the current chunk and start again!
						ret.add(temp);
						Log.d("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
						Log.d("JUST ADDED THIS CHUNK -> " + temp.getFrom());
						Log.d("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
						temp = new Chunk();
						temp.setAvailableFor(left.getCreatedAt(), right.getCreatedAt());
					}
				}
				
				//alternate
				first = !first;
				
				cal.add(Calendar.MILLISECOND, 5);
				i = i.add(BigInteger.ONE);
			}

		} catch (Exception e) {
			e.printStackTrace();
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
