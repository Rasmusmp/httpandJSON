package com.example.rasmus.httpandjson.util;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.rasmus.httpandjson.util.AlarmTask;

import java.util.Calendar;

/**
 * Created by Rasmus on 25-05-2015.
 * http://blog.blundell-apps.com/notification-for-a-user-chosen-time/
 */
public class ScheduleService extends Service{

    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder{
        ScheduleService getService(){
            return ScheduleService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i("ScheduleService", "Received start id " + startId + ": " + intent);

        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new ServiceBinder();

    public void setAlarm(Calendar c){
        new AlarmTask(this,c).run();
    }
}