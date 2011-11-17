package com.overseer.utils;

public class Log {
	public final static String LOGTAG = "overseer";
	public static final boolean debug = false;

	public static void v(String logMe) {
		if(debug){
			android.util.Log.v(LOGTAG, logMe);
		}
	}
	
	public static void d(String logMe){
		if(debug){
			android.util.Log.d(LOGTAG, logMe);
		}
	}

	public static void e(String logMe) {
		android.util.Log.e(LOGTAG, logMe);
	}

	public static void e(String logMe, Exception ex) {
		android.util.Log.e(LOGTAG, logMe, ex);
	}
	
	public static void w(String logMe){
		android.util.Log.w(LOGTAG, logMe);
	}
	
	public static void w(String tag, String logMe){
		android.util.Log.w(tag, logMe);		
	}

	public static void i(String logMe) {
		if(debug){
			android.util.Log.i(LOGTAG, logMe);
		}
	}
}
