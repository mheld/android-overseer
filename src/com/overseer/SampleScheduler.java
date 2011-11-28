package com.overseer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SampleScheduler extends BroadcastReceiver{
	
	public void onReceive(Context context, Intent intent) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, Sampler.class);
		i.putExtra("method", "sampleGps");
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
		
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
				System.currentTimeMillis(),
				(2 * 60 * 1000), //every 2 minutes
				pendingIntent);
	}
}
