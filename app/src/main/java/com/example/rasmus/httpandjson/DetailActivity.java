package com.example.rasmus.httpandjson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.transition.Slide;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
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
    TextView timeTextView;
    TextView typeTextView;

    TextView descriptionTextView;
    ImageView eventImg;

    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;
    Context context;



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
        //Typeface robotoBoldCondensedItalic = Typeface.createFromAsset(getAssets(), "fonts/Roboto-BoldCondensedItalic.ttf");
        //titleTextView.setTypeface(robotoBoldCondensedItalic);
        titleTextView.setText(name.toUpperCase());

        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        descriptionTextView.setText(description);


        timeTextView = (TextView) findViewById(R.id.timeTextView);
        timeTextView.setText("Kl: " + time);



        eventImg = (ImageView) findViewById(R.id.eventImage);

        typeTextView = (TextView) findViewById(R.id.typeTextView);
        // Set the place of the event in the place field
        switch (type){
            case "ballademad":
                typeTextView.setText("Ballademad");
                break;
            case "gastroscenen":
                typeTextView.setText("Gastroscenen");
                break;
            case "nordiskedraber":
                typeTextView.setText("Nordiske Dråber");
                break;
        }



        // Find the id of the image matching the type of event
        int resId = DetailActivity.this.getResources().getIdentifier(type+"_banner", "drawable", DetailActivity.this.getPackageName());
        // Set the image with the id found in the line above

       // Log.d("Nikolaj: ", ""+ resId);
       //

        eventImg.setImageResource(resId);

        LatLng eventLatLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));


        //GOOGLE MAPS

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        Marker event = map.addMarker(new MarkerOptions().position(eventLatLng)
                .title(name).snippet(description));


        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, 16));

        // Zoom in, animating the camera.
   //     map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
