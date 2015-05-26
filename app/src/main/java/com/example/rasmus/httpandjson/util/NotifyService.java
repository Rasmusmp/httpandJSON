package com.example.rasmus.httpandjson.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.rasmus.httpandjson.MainActivity;
import com.example.rasmus.httpandjson.ProgramFragment;
import com.example.rasmus.httpandjson.R;

/**
 * Created by Rasmus on 25-05-2015.
 *
 * This service is started when an Alarm has been raised
 *
 * We pop a notification into the status bar for the user to click on
 * When the user clicks the notification a new activity is opened
 *
 * @author paul.blundell: http://blog.blundell-apps.com/notification-for-a-user-chosen-time/
 *
 */
public class NotifyService extends Service implements ProgramFragment.Communicator {
    String msg = "Frederik Logging";

    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder {
        NotifyService getService(){
            return NotifyService.this;
        }
    }

       // Unique id to identify the notification.
    private static int NOTIFICATION = 0;
    // the text on the notification
    private static String name;
    // Name of an intent extra we can use to identify if this service was started to create a notification
    public static final String INTENT_NOTIFY = "com.example.rasmus.httpandjson.util.INTENT_NOTIFY";

    // The system notification manager
    private NotificationManager mNM;

    @Override
    public void respond(Bundle bundle) {
        NOTIFICATION = Integer.parseInt(bundle.getString("id"));
    }

    @Override
    public void onCreate() {
        Log.i(msg, "NotifyService - onCreate()");
        name = "An event";
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(msg, "NotifyService - Received start id " + startId + ": " + intent);
        Log.i(msg, "Intent info for NotifyService: " + intent.getStringExtra("Name"));
        name = intent.getStringExtra("Name");
        // If this service was started by our AlarmTask intent then we want to show our notification
        if (intent.getBooleanExtra(INTENT_NOTIFY, false)){
            showNotification();

        }
        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }

    // This is the object that receives interactions from clients
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new ServiceBinder();

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification(){
        // This is the 'title' of the notification
        CharSequence title = "Foodfestival Reminder";

        // This is the icon to use on the notification
        int icon = R.drawable.time;

        // This is the scrolling text of the notification
        CharSequence text = "" + name + " starts in 15 min";

        // What time to show on the notification
        long time = System.currentTimeMillis();

        Notification notification = new Notification(icon, text, time);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0); //MainActivity.class skal skiftes ud med det detailView som passer til notificationen

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, title, text, contentIntent);

        // Clear the notification when it is pressed
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        // Send the notification to the system.
        mNM.notify(NOTIFICATION, notification);

        // Store det NOTIFICATION id for later reference the key will be the name of the event in question
      //  SharedPreferences notificationId = PreferenceManager
       //         .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = getSharedPreferences("Events", MODE_PRIVATE).edit();
        prefsEditor.putInt(name ,NOTIFICATION);
        prefsEditor.apply();
        Log.i(msg, "NotifyService Applied Prefs: " + name + " and: " + NOTIFICATION);
        NOTIFICATION ++;
        // Stop the service when we are finished
        stopSelf();
    }
}
