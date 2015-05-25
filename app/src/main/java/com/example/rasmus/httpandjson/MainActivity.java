package com.example.rasmus.httpandjson;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rasmus.httpandjson.model.Event;
import com.example.rasmus.httpandjson.util.ProgramService;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;

/*
http://developer.android.com/guide/components/bound-services.html#Lifecycle
 */


public class MainActivity extends Activity implements ProgramFragment.Communicator{

    String msg = "Rasmus Logging";

    ProgramService ProgramService;
    ProgramBroadcastReceiver ProgramBroadcastReceiver;
    ArrayAdapter<String> adapter = null;
    ArrayAdapter<Event> eventAdapter = null;
    private ArrayList<Event> events = new ArrayList<Event>();
    Event[] storedEvents = null;
    // boolean to check if a service is bound.
    boolean isBound = false;
    Gson gson = new Gson();

    FragmentManager manager;
    ProgramFragment programFragment;

    private ProgressBar spinner;
    private ImageView imageLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(msg, "onCreate()");

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        imageLogo = (ImageView) findViewById(R.id.imageLogo);

        spinner.setVisibility(View.INVISIBLE);


        manager = getFragmentManager();

        programFragment = (ProgramFragment) manager.findFragmentById(R.id.programFragment);
        programFragment.setCommunicator(this);

        manager.beginTransaction().hide(programFragment).commit();

        restoreEvents();
        /*
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        //Gson gson = new Gson();
        String json = appSharedPrefs.getString("events","");
        storedEvents = gson.fromJson(json, Event[].class);
        //Log.d(msg, "Old events: " + gson.toJson(storedEvents));
        */


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(msg, "onStart()");
        restoreEvents();

        // Bind to localService
        Intent intent = new Intent(this, ProgramService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(msg, "onStop()");

        storeEvents();

        /*
        // Store the list of objects on the phone
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(events);
        prefsEditor.putString("events", json);
        prefsEditor.commit();
        Log.d(msg, "Events stored");
        */

        // unbind from the service
        if(isBound) {
            unbindService(myConnection);
            isBound = false;
        }
    }

    public void getProgram(){
        if(isBound && isNetworkConnected() && events.isEmpty()){
            if (eventAdapter==null) {
                spinner.setVisibility(View.VISIBLE);
                imageLogo.setVisibility(View.VISIBLE);
            }
            Log.d(msg, "Main - Internet");
            manager.beginTransaction().hide(programFragment).commit();
            ProgramService.fetchJSON();
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
            getProgram();
        }else if (!isBound){
            Toast.makeText(this, "Please 'Bind' service", Toast.LENGTH_SHORT).show();
        }else if (!isNetworkConnected()){
            Toast.makeText(this, "Please connect your phone to the internet", Toast.LENGTH_LONG).show();
        }
    }

    public void unBindService(View v){
/*        // unbind from the service
        if(isBound) {
            unbindService(myConnection);
            isBound = false;
            Toast.makeText(this, "Service is unBound", Toast.LENGTH_SHORT).show();
        }
*/
        /*
        // http://androidcodemonkey.blogspot.dk/2011/07/store-and-get-object-in-android-shared.html
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(events);
        prefsEditor.putString("events", json);
        prefsEditor.commit();
        Log.d(msg, "Events stored");
        */
    }

    public void BindService(View v){
/*        if(!isBound){
            // Bind to localService
            Intent intent = new Intent(this, ProgramService.class);
            bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
            Toast.makeText(this, "Service is Bound", Toast.LENGTH_SHORT).show();
        }
*/
        restoreEvents();
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("events","");
        Event[] oldEvents = gson.fromJson(json, Event[].class);
        //Log.d(msg, "Old events: " + gson.toJson(oldEvents));
    }

    public void clearList(View v){
        storedEvents = null;
        if (!events.isEmpty()){
            events.clear();
            programFragment.changeData(events);
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
            ProgramService.LocalBinder binder = (ProgramService.LocalBinder) service;
            ProgramService = binder.getService();
            isBound = true;

            if (storedEvents!=null && storedEvents.length > 0) {
                spinner.setVisibility(View.GONE);
                try {
                    Log.d(msg, "Local");
                    updateEventListView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateView();

            } else {
                getProgram();
                //updateView();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void respond(Bundle bundle) {

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtras(bundle);


        if (Build.VERSION.SDK_INT >= 16) {
            Bundle slideAnimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),
                    R.anim.slide_in_from_right, // Enter animation
                    R.anim.slide_out_to_left).toBundle(); // Exit animation

            startActivity(intent, slideAnimation);
        } else { startActivity(intent); }


    }


    private class ProgramBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(ProgramService.RESULT_RETURNED_FROM_SERVICE) == 0){
                spinner.setVisibility(View.GONE);
                try {
                    updateEventListView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateView();
            } else {
                Toast.makeText(context, "Host not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateView(){
        imageLogo.setVisibility(View.GONE);
        manager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).show(programFragment).commit();

    }

    private void updateEventListView() throws JSONException {

        ProgramFragment programFragment = (ProgramFragment) getFragmentManager().findFragmentById(R.id.programFragment);
        if (ProgramService != null && eventAdapter == null){

            if (storedEvents!=null && storedEvents.length > 0) {
                //Log.d(msg, "storedEvents: " + gson.toJson(storedEvents));
                ProgramService.createEventArray(gson.toJson(storedEvents));
            }

            events = ProgramService.getCurrentEventList();
            //Log.d(msg, "Main - events:" + events);
            programFragment.changeData(events); //make sure we keep old program
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
        filter = new IntentFilter(ProgramService.RESULT_RETURNED_FROM_SERVICE);
        filter.addAction(ProgramService.ERROR_CALL_SERVICE);
        ProgramBroadcastReceiver = new ProgramBroadcastReceiver();
        registerReceiver(ProgramBroadcastReceiver, filter);

    }

    public void storeEvents(){
// Store the list of objects on the phone
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(events);
        prefsEditor.putString("events", json);
        prefsEditor.apply();
        //prefsEditor.commit();
        Log.d(msg, "Events stored");
    }

    public void restoreEvents(){

        // http://androidcodemonkey.blogspot.dk/2011/07/store-and-get-object-in-android-shared.html
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        //Gson gson = new Gson();
        String json = appSharedPrefs.getString("events","");
        storedEvents = gson.fromJson(json, Event[].class);
        //Log.d(msg, "Old events: " + gson.toJson(storedEvents));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(msg, "onDestroy()");
    }
}
