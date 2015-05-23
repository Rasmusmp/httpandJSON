package com.example.rasmus.httpandjson.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rasmus.httpandjson.R;
import com.example.rasmus.httpandjson.model.Event;

import java.util.ArrayList;

/**
 * Created by Rasmus on 23-05-2015.
 * http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
 */
public class EventAdapter extends ArrayAdapter<Event> {

    Context context;
    int layoutResourceId;
    ArrayList<Event> data = null;

    public EventAdapter(Context context, int layoutResourceId, ArrayList<Event> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        View row = convertView;
        EventHolder holder = null;

        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new EventHolder();
            holder.listImg = (ImageView) row.findViewById(R.id.listImageView);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtTime = (TextView) row.findViewById(R.id.txtTime);

            row.setTag(holder);
        } else{
            holder = (EventHolder) row.getTag();
        }

        Event event = data.get(position);
        holder.txtTitle.setText(event.getName());

        int resId = context.getResources().getIdentifier(event.getType(),"drawable", context.getPackageName());
        holder.listImg.setImageResource(resId);

        if (!event.getTime().isEmpty()) {
            holder.txtTime.setText("kl. " + event.getTime());
        } else {
            holder.txtTime.setText("All day");
        }

        return row;
    }

    static class EventHolder {
        ImageView listImg;
        TextView txtTitle;
        TextView txtTime;
    }
}
