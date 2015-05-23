package com.example.rasmus.httpandjson;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public class iTogService extends Service {

    String msg = "Rasmus Logging: ";
    //String JSONData;
    //JSONObject reader;
    //String JSONstring = "http://stog.itog.dk/itog/action/list/format/json";
    public static final String RESULT_RETURNED_FROM_SERVICE = "Result_Returned_From_Service";
    public static final String ERROR_CALL_SERVICE = "Error_Call_Service";

    private ArrayList<String> listItems = new ArrayList<String>();


    // Binder given to clients
    private final IBinder myBinder = new LocalBinder();

    private final Random RandomGenerator = new Random();

    public iTogService() {

    }

    public class LocalBinder extends Binder {
        iTogService getService(){
            return iTogService.this;
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

                    getStationList();
                    //Log.d(msg, "Station list: " + listItems);
            }
        });
        thread.start();
    }

    private void getStationList(){
        URI myURI = null;

        try {
            myURI = new URI("http://stog.itog.dk/itog/action/list/format/json");
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

            JSONArray jsonArray = new JSONArray(SB.toString());

            listItems.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                listItems.add(jsonObject.getString("name"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent listDone = new Intent(RESULT_RETURNED_FROM_SERVICE);
        sendBroadcast(listDone);

    }

    public ArrayList<String> getCurrentStationList(){
        return listItems;
    }

}
