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
        int position = intent.getIntExtra("position",0);

        detailTitle = (TextView) findViewById(R.id.detaiTitle);

        detailTitle.append(" " + position);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
