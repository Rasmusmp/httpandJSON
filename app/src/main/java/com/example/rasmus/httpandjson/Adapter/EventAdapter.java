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
import android.widget.Toast;

import com.example.rasmus.httpandjson.R;
import com.example.rasmus.httpandjson.model.Event;
import com.example.rasmus.httpandjson.util.ScheduleClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by Rasmus on 23-05-2015.
 * http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
 */
public class EventAdapter extends ArrayAdapter<Event> {
    String msg = "Rasmus Logging";

    Context context;
    int layoutResourceId;
    ArrayList<Event> data = null;

    private ScheduleClient scheduleClient;

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


        // Create a new service client and 'hopefully' bind our adapter to this service
        scheduleClient = new ScheduleClient(getContext());
        scheduleClient.doBindService();

        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new EventHolder();
            holder.listImg = (ImageView) row.findViewById(R.id.listImageView);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtTime = (TextView) row.findViewById(R.id.txtTime);
            holder.txtPlace = (TextView) row.findViewById(R.id.txtPlace);
            holder.reminderBtn = (ImageButton) row.findViewById(R.id.reminderBtn);

            // If the event is all festival, hide the reminder button
            if (data.get(position).getDate().isEmpty()|| data.get(position).getTime().isEmpty()){
                holder.reminderBtn.setVisibility(View.INVISIBLE);
            }


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

                    // Get the date from our event
                    String date = data.get(position).getDate();
                    // Use StringTokenizer to split string into day, month, and year
                    StringTokenizer dateTokens = new StringTokenizer(date, "/");
                    String day = dateTokens.nextToken();
                    String month = dateTokens.nextToken();
                    String year = "20" + dateTokens.nextToken();

                    // Get the time of our event
                    String time = data.get(position).getTime();
                    // Use StringTokenizer to split string into hours and minutes
                    StringTokenizer timeTokens = new StringTokenizer(time, ":");
                    String hours = timeTokens.nextToken();
                    String minutes = timeTokens.nextToken();

                    if (!data.get(position).getReminder()) {
                        data.get(position).setReminder(true);
                        finalHolder.reminderBtn.setImageResource(R.drawable.reminder_true);

                        // Create a new calendar
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Integer.parseInt(year),
                                // In java.util.Calendar, the month value is 0-based, so we subtract 1 from the month to get the right format
                                (Integer.parseInt(month)-1),
                                Integer.parseInt(day),
                                Integer.parseInt(hours),
                                Integer.parseInt(minutes),
                                0);
                        scheduleClient.setAlarmForNotification(calendar);
                        Log.d(msg, "Alarm " + data.get(position)+ " is set");
                        Toast.makeText(context, "Notification set for: "+ day +"/"+ (month) +"/"+ year + " " + hours + ":" + minutes, Toast.LENGTH_SHORT).show();

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

        // set the state of the reminder button
        if (data.get(position).getReminder() == true){
            holder.reminderBtn.setImageResource(R.drawable.reminder_true);
        } else if (data.get(position).getReminder() == false){
            holder.reminderBtn.setImageResource(R.drawable.reminder_false);
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
            holder.txtTime.setText("Hele festivalen");
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
