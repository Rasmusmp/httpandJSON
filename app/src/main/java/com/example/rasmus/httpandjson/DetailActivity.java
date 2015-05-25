package com.example.rasmus.httpandjson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Window;
import android.widget.TextView;


public class DetailActivity extends Activity {

    TextView detailTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();

        Bundle b = intent.getExtras();

        String name = b.getString("name");
        String latitude = b.getString("latitude");
        String longitude = b.getString("longitude");
        String description = b.getString("description");
        String date = b.getString("date");
        String id = b.getString("id");
        String time = b.getString("time");
        String type = b.getString("type");

        detailTitle = (TextView) findViewById(R.id.detaiTitle);

        detailTitle.append("\n Event:                  " + name);
        detailTitle.append("\n Koordinater:          " + latitude+" : "+longitude);
        detailTitle.append("\n Beskrivelse:      " + description);
        detailTitle.append("\n Dato og tid:          " + date + " : " + time);
        detailTitle.append("\n id:                         " + id);
        detailTitle.append("\n Eventtype:            " + type);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
