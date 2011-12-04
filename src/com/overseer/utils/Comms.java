package com.overseer.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.provider.CallLog.*;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class Comms {

	static String[] strFields = {
			Calls.NUMBER, 
			Calls.TYPE,
			Calls.CACHED_NAME,
			Calls.CACHED_NUMBER_TYPE,
	};
	
	static String strOrder = Calls.DATE + " DESC"; 

	public static List<String> getComms(Context context){
		Cursor callCursor = context.getContentResolver().query(
				Calls.CONTENT_URI,
				strFields,
				null,
				null,
				strOrder
				);
		
		return getComms(callCursor);
	}
	
	public static List<String> getCommsBetween(Context context, Date left, Date right){
		Cursor callCursor = context.getContentResolver().query(
				Calls.CONTENT_URI,
				strFields,
				Calls.DATE+" > "+left.getTime() + " and " + Calls.DATE + " < " + right.getTime(),
				null,
				strOrder
				);
		
		return getComms(callCursor);
	}
	
	public static List<String> getComms(Cursor callCursor){
		try {
			ArrayList<String> coordinates = new ArrayList<String>();

			callCursor.moveToFirst();
			for (int i = 0; i < callCursor.getCount(); i++) {
				coordinates.add(callCursor.getString(
						callCursor.getColumnIndex(
								Calls.CACHED_NAME)));
				callCursor.moveToNext();
			}
			callCursor.close();

			return coordinates;

		} catch (SQLException e) {
			Log.e("Exception on query", e);
			return new ArrayList<String>();
		} finally{
			callCursor.close();
		}
	}

}
