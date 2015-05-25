package com.example.rasmus.httpandjson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;


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

}
