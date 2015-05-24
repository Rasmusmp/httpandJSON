package com.example.rasmus.httpandjson.model;

import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rasmus on 23-05-2015.
 */
public class Event {

    private String name;
    private int id;
    private String date;
    private String time;
    private String description;
    private String latitude;
    private String longitude;
    private String type;
    private boolean reminder;

    public Event(String name, int id, String date, String time, String description, String latitude, String longitude, String type, boolean reminder) {

        this.name = name;
        this.id = id;
        this.date = date;
        this.time = time;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.reminder = reminder;

    }

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public int getId(){return id;}
    public void setId(int id){this.id = id;}

    public String getDate(){return  date;}
    public void setDate(String date){this.date = date;}

    public String getTime(){return time;}
    public void setTime(String time){this.time = time;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public String getLatitude(){return latitude;}
    public void setLatitude(String latitude){this.latitude = latitude;}

    public String getLongitude(){return longitude;}
    public void setLongitude(String longitude){this.longitude = longitude;}

    public String getType(){return type;}
    public void setType(String type){this.type = type;}

    public boolean getReminder(){return reminder;}
    public void setReminder(boolean reminder){this.reminder = reminder;}

    @Override
    public String toString() {

        String stringTime = null;

        if (!this.time.isEmpty()){
            stringTime = "kl. " + this.time;
        }else { stringTime ="All day"; }

        return "" + this.name + " " + stringTime;
    }
}
