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

import org.w3c.dom.Text;


public class DetailActivity extends Activity {

    TextView titleTextView;
    TextView descriptionTextView;

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
        String reminder = b.getString("reminder");

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setText(name);

        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(description);


        /**
        detailTitle.append("\n Event:                  " + name);
        detailTitle.append("\n Koordinater:          " + latitude+" : "+longitude);
        detailTitle.append("\n Beskrivelse:      " + description);
        detailTitle.append("\n Dato og tid:          " + date + " : " + time);
        detailTitle.append("\n id:                         " + id);
        detailTitle.append("\n Eventtype:            " + type);
**/

        LatLng eventLatLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));


        //GOOGLE MAPS

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        Marker event = map.addMarker(new MarkerOptions().position(eventLatLng)
                .title(name).snippet(description));


        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
