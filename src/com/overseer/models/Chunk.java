package com.overseer.models;

import java.util.Date;

import android.database.Cursor;

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
}
