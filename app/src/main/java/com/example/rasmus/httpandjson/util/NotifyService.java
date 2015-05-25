package com.example.rasmus.httpandjson.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.rasmus.httpandjson.MainActivity;
import com.example.rasmus.httpandjson.R;

/**
 * Created by Rasmus on 25-05-2015.
 * http://blog.blundell-apps.com/notification-for-a-user-chosen-time/
 */
public class NotifyService extends Service {

    public class ServiceBinder extends Binder {
        NotifyService getService(){
            return NotifyService.this;
        }
    }

    private static final int NOTIFICATION = 123;

    public static final String INTENT_NOTIFY = "com.example.rasmus.httpandjson.util.INTENT_NOTIFY";

    private NotificationManager mNM;

    @Override
    public void onCreate() {
        Log.i("NotifyService", "onCreate()");
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getBooleanExtra(INTENT_NOTIFY, false)){
            showNotification();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new ServiceBinder();

    private void showNotification(){
        // This is the 'title' of the notification
        CharSequence title = "Alarm!!";

        // This is the icon to use on the notification
        int icon = R.drawable.time;

        // This is the scrolling text of the notification
        CharSequence text = "Your notification time is upon us.";

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

        // Stop the service when we are finished
        stopSelf();
    }
}
