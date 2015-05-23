package com.example.rasmus.httpandjson;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rasmus.httpandjson.Adapter.EventAdapter;
import com.example.rasmus.httpandjson.model.Event;
import com.example.rasmus.httpandjson.util.iTogService;

/*
http://developer.android.com/guide/components/bound-services.html#Lifecycle
 */

//fuck af

public class MainActivity extends ListActivity {

    String msg = "Rasmus Logging: ";

    com.example.rasmus.httpandjson.util.iTogService iTogService;
    iTogBroadcastReceiver iTogBroadcastReceiver;
    ArrayAdapter<String> adapter = null;
    ArrayAdapter<Event> eventAdapter = null;
    private String[] listItems = null;
    // boolean to check if a service is bound.
    boolean isBound = false;

    private ProgressBar spinner;


    String JSONstring = "http://stog.itog.dk/itog/action/list/format/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
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
        if(isBound && isNetworkConnected()){
            if (eventAdapter==null) {
                spinner.setVisibility(View.VISIBLE);
            }
            iTogService.fetchJSON();
        }else if (!isBound){
            Toast.makeText(this, "Please 'Bind' service", Toast.LENGTH_SHORT).show();
        }else if (!isNetworkConnected()){
            Toast.makeText(this, "Please connect your phone to the internet", Toast.LENGTH_LONG).show();
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
        if (eventAdapter != null){
            eventAdapter = null;
            setListAdapter(eventAdapter);
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
                //updateItogListView();
                spinner.setVisibility(View.GONE);
                updateEventListView();
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

    private void updateEventListView(){
        if (iTogService != null && eventAdapter == null){
             eventAdapter = new EventAdapter(this,
                    R.layout.listview_item_row,
                    iTogService.getCurrentEventList());

            setListAdapter(eventAdapter);

            /*
            eventAdapter = new ArrayAdapter<Event>(this,
                    android.R.layout.simple_list_item_1,
                    iTogService.getCurrentEventList());
            setListAdapter(eventAdapter);
            */

        }

    }

    public boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null){
            return false;
        } else { return true; }
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
