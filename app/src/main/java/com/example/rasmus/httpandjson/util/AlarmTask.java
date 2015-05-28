package com.example.rasmus.httpandjson.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Rasmus on 25-05-2015.
 * http://blog.blundell-apps.com/notification-for-a-user-chosen-time/
 */
public class AlarmTask implements Runnable{
    String msg = "Frederik Logging";

    // The date selected for the alarm
    private final Calendar date;
    // The text on the notification
    private final String info;
    // The android system alarm manager
    private final AlarmManager am;
    private static final long ONE_MIN_AS_MILLISEC = 60000;
    // Your context to retrieve the alarm manager from
    private final Context context;

    public AlarmTask(Context context, Calendar date, String info) {
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
        this.info = info;
    }

    @Override
    public void run() {
        // Request to start our service when the alarm date is upon us
        // We don't start an activity as we just want to pop up a notification into the system bar not a full activity
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        intent.putExtra("Name", info);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again.
        // Quickly thinking, the way round this would be to have your app be notified of the phone starting up and re-starting your alarms.
        SharedPreferences prefsEditor = context.getSharedPreferences("Time", context.MODE_PRIVATE);
        int timeInMin = prefsEditor.getInt(info, 0);
        Log.d(msg, "Time received: " + timeInMin);
        am.set(AlarmManager.RTC_WAKEUP, (date.getTimeInMillis() - (ONE_MIN_AS_MILLISEC*timeInMin)), pendingIntent);
    }
}
