package com.example.rasmus.httpandjson;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rasmus.httpandjson.model.Event;
import com.example.rasmus.httpandjson.util.iTogService;

import java.util.ArrayList;

/*
http://developer.android.com/guide/components/bound-services.html#Lifecycle
 */


public class MainActivity extends Activity implements ProgramFragment.Communicator{

    String msg = "Rasmus Logging: ";

    com.example.rasmus.httpandjson.util.iTogService iTogService;
    iTogBroadcastReceiver iTogBroadcastReceiver;
    ArrayAdapter<String> adapter = null;
    ArrayAdapter<Event> eventAdapter = null;
    private ArrayList<Event> events = new ArrayList<Event>();
    // boolean to check if a service is bound.
    boolean isBound = false;

    FragmentManager manager;
    ProgramFragment programFragment;

    private ProgressBar spinner;
    private ImageView imageLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        imageLogo = (ImageView) findViewById(R.id.imageLogo);

        spinner.setVisibility(View.INVISIBLE);

        manager = getFragmentManager();

        programFragment = (ProgramFragment) manager.findFragmentById(R.id.programFragment);
        programFragment.setCommunicator(this);

        manager.beginTransaction().hide(programFragment).commit();
    }

    public void updateView(){
        imageLogo.setVisibility(View.GONE);
        manager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).show(programFragment).commit();

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

    public void getProgram(){
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
            //setListAdapter(eventAdapter);
        }
    }


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
            getProgram();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void respond(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("position", position);

        if (Build.VERSION.SDK_INT >= 16) {
            Bundle slideAnimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),
                    R.anim.slide_in_from_right, // Enter animation
                    R.anim.slide_out_to_left).toBundle(); // Exit animation

            startActivity(intent, slideAnimation);
        } else { startActivity(intent); }
    }


    private class iTogBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(iTogService.RESULT_RETURNED_FROM_SERVICE) == 0){
                spinner.setVisibility(View.GONE);
                updateEventListView();
                updateView();
            } else {
                Toast.makeText(context, "Host not available", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateEventListView(){

        ProgramFragment programFragment = (ProgramFragment) getFragmentManager().findFragmentById(R.id.programFragment);
        if (iTogService != null && eventAdapter == null){

            events = iTogService.getCurrentEventList();
            programFragment.changeData(events);
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
