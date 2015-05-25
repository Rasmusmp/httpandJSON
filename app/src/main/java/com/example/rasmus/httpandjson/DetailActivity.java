package com.example.rasmus.httpandjson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class DetailActivity extends Activity {

    TextView detailTitle;


    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;


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


        //GOOGLE MAPS

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
                .title("Hamburg"));
        Marker kiel = map.addMarker(new MarkerOptions()
                .position(KIEL)
                .title("Kiel")
                .snippet("Kiel is cool")
               );

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
