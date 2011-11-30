package com.overseer.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class Comms {

	static String[] strFields = {
			android.provider.CallLog.Calls.NUMBER, 
			android.provider.CallLog.Calls.TYPE,
			android.provider.CallLog.Calls.CACHED_NAME,
			android.provider.CallLog.Calls.CACHED_NUMBER_TYPE,
	};
	
	static String strOrder = android.provider.CallLog.Calls.DATE + " DESC"; 

	public static List<String> getComms(Context context){
		Cursor callCursor = context.getContentResolver().query(
				android.provider.CallLog.Calls.CONTENT_URI,
				strFields,
				null,
				null,
				strOrder
				);
		
		try {
			ArrayList<String> coordinates = new ArrayList<String>();

			callCursor.moveToFirst();
			for (int i = 0; i < callCursor.getCount(); i++) {
				coordinates.add(callCursor.getString(
						callCursor.getColumnIndex(
								android.provider.CallLog.Calls.CACHED_NAME)));
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
