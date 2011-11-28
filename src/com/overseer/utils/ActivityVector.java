package com.overseer.utils;

import java.util.ArrayList;

public class ActivityVector {
	private double x;
	private double y;
	private double z;
	
	public ActivityVector(double x, double y, double z){
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
	
	public static double averageVectors(ArrayList<ActivityVector> vectors){
		double average = 0;
		
		// take the magnitude of all the vectors
		for(ActivityVector v : vectors){
			average = average + Math.sqrt( 
					Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2) + Math.pow(v.getZ(), 2));
		}
		
		// average the vector-pool
		if(vectors.size() == 0){
			//don't bother
		}else{
			average = average/vectors.size();
		}
		
		return average;
	}

}
