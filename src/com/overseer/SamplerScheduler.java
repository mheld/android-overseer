package com.overseer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SamplerScheduler extends BroadcastReceiver{
	
	public void onReceive(Context context, Intent intent) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, SamplerService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
		
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
				System.currentTimeMillis(),
				(15 * 60 * 1000), //every 15 minutes
				pendingIntent);
	}
}
