package com.example.rasmus.httpandjson.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.Calendar;

/**
 * Created by Rasmus on 25-05-2015.
 * http://blog.blundell-apps.com/notification-for-a-user-chosen-time/
 */
public class ScheduleClient {

    private ScheduleService mBoundService;
    private Context mContext;
    private boolean mIsBound;

    public ScheduleClient(Context context){
        mContext = context;
    }

    public void doBindService(){
        mContext.bindService(new Intent(mContext, ScheduleService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((ScheduleService.ServiceBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className){
            mBoundService = null;
        }
    };

    public void setAlarmForNotification(Calendar c){
        mBoundService.setAlarm(c);
    }

    public void doUnbindService(){
        if (mIsBound){
            mContext.unbindService(mConnection);
            mIsBound = false;
        }
    }
}
