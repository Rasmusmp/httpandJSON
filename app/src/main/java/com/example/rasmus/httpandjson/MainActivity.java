package com.example.rasmus.httpandjson;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

/*
http://developer.android.com/guide/components/bound-services.html#Lifecycle
 */


public class MainActivity extends ListActivity {

    String msg = "Rasmus Logging: ";

    iTogService iTogService;
    iTogBroadcastReceiver iTogBroadcastReceiver;
    ArrayAdapter<String> adapter = null;
    private String[] listItems = null;
    // boolean to check if a service is bound.
    boolean isBound = false;

    String JSONstring = "http://stog.itog.dk/itog/action/list/format/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Bind to localService
        Intent intent = new Intent(this, iTogService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // unbind from the service
        if(isBound) {
            unbindService(myConnection);
            isBound = false;
        }
    }

    public void onButtonClick(View v){
        if(isBound){
            iTogService.fetchJSON();
        }else{  Toast.makeText(this, "Please 'Bind' service", Toast.LENGTH_SHORT).show();

        }
    }

    public void unBindService(View v){
        // unbind from the service
        if(isBound) {
            unbindService(myConnection);
            isBound = false;
            Toast.makeText(this, "Service is unBound", Toast.LENGTH_SHORT).show();
        }
    }

    public void BindService(View v){
        if(!isBound){
            // Bind to localService
            Intent intent = new Intent(this, iTogService.class);
            bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
            Toast.makeText(this, "Service is Bound", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearList(View v){
        if (adapter != null){
            setListAdapter(null);
        }
    }

    //hej


    /*
    ServiceConnection monitors the connection with the service.
     */
    private ServiceConnection myConnection = new ServiceConnection() {
        @Override

        /*
        The onServiceConnection delivers the IBinder that is used to communicate with the service.
         */
        public void onServiceConnected(ComponentName name, IBinder service) {

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            iTogService.LocalBinder binder = (iTogService.LocalBinder) service;
            iTogService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };


    private class iTogBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(iTogService.RESULT_RETURNED_FROM_SERVICE) == 0){
                updateItogListView();
            } else {
                Toast.makeText(context, "Host not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateItogListView(){
        if (iTogService != null){
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    iTogService.getCurrentStationList());
            setListAdapter(adapter);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(msg, "onResume()");

        IntentFilter filter;
        filter = new IntentFilter(iTogService.RESULT_RETURNED_FROM_SERVICE);
        filter.addAction(iTogService.ERROR_CALL_SERVICE);
        iTogBroadcastReceiver = new iTogBroadcastReceiver();
        registerReceiver(iTogBroadcastReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
