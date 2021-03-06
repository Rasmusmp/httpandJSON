package com.example.rasmus.httpandjson.util;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.rasmus.httpandjson.model.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public class ProgramService extends Service {

    String msg = "Rasmus Logging: ";
    //String JSONData;
    //JSONObject reader;
    String JSONstring = "http://nhaulrik.dk/food_festival.json";
    public static final String RESULT_RETURNED_FROM_SERVICE = "Result_Returned_From_Service";
    public static final String ERROR_CALL_SERVICE = "Error_Call_Service";

    private ArrayList<Event> events = new ArrayList<Event>();


    // Binder given to clients
    public final IBinder myBinder = new LocalBinder();

    private final Random RandomGenerator = new Random();

    public ProgramService() {

    }

    public class LocalBinder extends Binder {
        public ProgramService getService(){
            return ProgramService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public int getRandomNumber(){
        return RandomGenerator.nextInt(100);
    }


    public void fetchJSON(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    getStationList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Log.d(msg, "Station list: " + listItems);
            }
        });
        thread.start();
    }

    public void getStationList() throws JSONException {
        URI myURI = null;

        try {
            myURI = new URI(JSONstring);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet getMethod = new HttpGet(myURI);
        HttpResponse webServerResponse = null;

        try {
            webServerResponse = httpClient.execute(getMethod);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity httpEntity = webServerResponse.getEntity();

        if(httpEntity != null) try {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpEntity.getContent()));

            StringBuilder SB = new StringBuilder();
            String data;
            while ((data = in.readLine()) != null) {
                SB.append(data);
            }

            createEventArray(SB.toString());
            /*
            JSONArray jsonArray = new JSONArray(SB.toString());

            //events.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JS = jsonArray.getJSONObject(i);

                Event event = new Event(JS.getString("name"),
                                        JS.getInt("id"),
                                        JS.getString("date"),
                                        JS.getString("time"),
                                        JS.getString("description"),
                                        JS.getString("lat"),
                                        JS.getString("long"),
                                        JS.getString("type"),
                                        false);

                events.add(event);


                //Log.d(msg, "Event list: " + events.get(i).getDescription());
                //Log.d(msg, "Is null?: " + events.get(i).getTime().isEmpty());
            }
            */



        } catch (IOException e) {
            e.printStackTrace();
        }



        Intent listDone = new Intent(RESULT_RETURNED_FROM_SERVICE);
        sendBroadcast(listDone);

    }

    public void createEventArray(String string) throws JSONException {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(string);
            events.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JS = jsonArray.getJSONObject(i);

                Event event = new Event(JS.getString("name"),
                        JS.getInt("id"),
                        JS.getString("date"),
                        JS.getString("time"),
                        JS.getString("description"),
                        JS.getString("latitude"),
                        JS.getString("longitude"),
                        JS.getString("type"),
                        JS.getBoolean("reminder"));

                events.add(event);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }



    }

    public ArrayList<Event> getCurrentEventList(){
        //Log.d(msg, "Service - events:" + events);
        return events;
    }

}
