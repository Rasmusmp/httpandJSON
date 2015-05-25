package com.example.rasmus.httpandjson.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
    String msg = "Rasmus Logging: ";

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
    public int getCount() {
        if(data!=null){
            return data.size();
        } else { return 0; }
    }

    @Override
    public Event getItem(int position) {
        if(data!=null){
            return data.get(position);
        } else {return null; }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
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
            holder.txtPlace = (TextView) row.findViewById(R.id.txtPlace);
            holder.reminderBtn = (ImageButton) row.findViewById(R.id.reminderBtn);



            /*
            * Make buttonListener for reminder btn in listView.
            * If clicked and reminder is not set(false), set reminder to true or else keep false
            * and update/set picture/view accordingly.
            * Maybe use setOnCheckChangedListener instead.
            */
            final EventHolder finalHolder = holder;
            holder.reminderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!data.get(position).getReminder()) {
                        data.get(position).setReminder(true);
                        finalHolder.reminderBtn.setImageResource(R.drawable.reminder_true);
                    } else {
                        data.get(position).setReminder(false);
                        finalHolder.reminderBtn.setImageResource(R.drawable.reminder_false);
                    }
                }
            });

            row.setTag(holder);
        } else{
            holder = (EventHolder) row.getTag();
        }

        // Get data from event array at a given position
        Event event = data.get(position);

        // Set the name of the event in the title field
        holder.txtTitle.setText(event.getName());

        // Find the id of the image matching the type of event
        int resId = context.getResources().getIdentifier(event.getType(),"drawable", context.getPackageName());
        // Set the image with the id found in the line above
        holder.listImg.setImageResource(resId);

        // Check to see if time variable is empty. This means the event lasts all day.
        if (!event.getTime().isEmpty()) {
            holder.txtTime.setText("kl. " + event.getTime());
        } else {
            holder.txtTime.setText("All day");
        }

        // Set the place of the event in the place field
        switch (event.getType()){
            case "ballademad":
                holder.txtPlace.setText("Ballademad");
                break;
            case "gastroscenen":
                holder.txtPlace.setText("Gastroscenen");
                break;
            case "nordiskedraber":
                holder.txtPlace.setText("Nordiske Dråber");
                break;
        }

        return row;
    }

    static class EventHolder {
        ImageView listImg;
        TextView txtTitle;
        TextView txtTime;
        TextView txtPlace;
        ImageButton reminderBtn;
    }
}
