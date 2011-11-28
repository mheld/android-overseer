package com.overseer;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class QuestionnaireScheduler extends BroadcastReceiver{
	
	public void onReceive(Context context, Intent intent) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, Viewer.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 20);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
				cal.getTimeInMillis(),
				(24 * 60 * 60 * 1000), //every 24 hours at 8pm
				pendingIntent);
	}
}
