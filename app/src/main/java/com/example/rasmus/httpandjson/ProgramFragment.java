package com.example.rasmus.httpandjson;

import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rasmus.httpandjson.Adapter.EventAdapter;
import com.example.rasmus.httpandjson.model.Event;
import com.example.rasmus.httpandjson.util.NotifyService;
import com.example.rasmus.httpandjson.util.ProgramService;
import com.example.rasmus.httpandjson.util.ScheduleClient;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.StringTokenizer;

public class ProgramFragment extends Fragment implements EventAdapter.Listener {
    String msg = "Rasmus Logging";

    ArrayAdapter<Event> eventAdapter = null;
    ProgramService ProgramService;
    ArrayList<Event> eventList;


    Communicator communicator;
    ListView programList;

    // This is a handle so that we can call methods on our notification service
    private ScheduleClient scheduleClient;

    private NotificationManager notificationManager;
    public ProgramFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create the Popup Menu for selection of alarm time

        //timeselecter.inflate();
        // Create a new service client and bind our activity to this service
        scheduleClient = new ScheduleClient(this.getActivity());
        scheduleClient.doBindService();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.program_fragment, container, false);

        programList = (ListView) view.findViewById(R.id.programList);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void changeData(final ArrayList<Event> events){
        final EventAdapter eventAdapter = new EventAdapter(getActivity(), R.layout.listview_item_row, events, this);
        programList.setAdapter(eventAdapter);

        programList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

             Log.d(msg, "Position: " + position);

             Event e = eventAdapter.getItem(position);
             eventList = events;

             Log.d(msg, "eventE "+ e);

             Bundle b = new Bundle();

             b.putString("latitude",e.getLatitude());
             b.putString("longitude",e.getLongitude());
             b.putString("description",e.getDescription());
             b.putString("date",e.getDate());
             b.putString("name",e.getName());
             b.putString("id",""+e.getId());
             b.putString("time",e.getTime());
             b.putString("type",e.getType());
             b.putString("reminder", ""+e.getReminder());

            communicator.respond(b);
            }
        });

    }


    @Override
    public void onStateChange(boolean state, int position, ArrayList<Event> events) {

        Log.d(msg, "StateChanged: " + events);
        Log.d(msg, "StateChanged: " + state + ", " + position);
        // Get the date from our event
        String date = events.get(position).getDate();
        // Use StringTokenizer to split string into day, month, and year
        StringTokenizer dateTokens = new StringTokenizer(date, "/");
        String day = dateTokens.nextToken();
        String month = dateTokens.nextToken();
        String year = "20" + dateTokens.nextToken();

        // Get the time of our event
        String time = events.get(position).getTime();
        // Use StringTokenizer to split string into hours and minutes
        StringTokenizer timeTokens = new StringTokenizer(time, ":");
        String hours = timeTokens.nextToken();
        String minutes = timeTokens.nextToken();

        // Create calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(year),
                // In java.util.Calendar, the month value is 0-based, so we subtract 1 from the month to get the right format
                (Integer.parseInt(month)-1),
                Integer.parseInt(day),
                Integer.parseInt(hours),
                Integer.parseInt(minutes),
                0);

        // Get notification information from the event
        String info = events.get(position).getName();

        if (state){
            // Create notification - is the overall goal of this state. There's a few steps involved though, as we would like to set the time.
            // Make popup menu (and make it appear at the right position, of course)

            PopupMenu timeselecter = new PopupMenu(getActivity(), programList.getChildAt(position).findViewById(R.id.reminderBtn));
            // Populate our menu
            timeselecter.getMenu().add(Menu.NONE, 5, Menu.NONE, "5 min in advance");
            timeselecter.getMenu().add(Menu.NONE, 10, Menu.NONE, "10 min in advance");
            timeselecter.getMenu().add(Menu.NONE, 15, Menu.NONE, "15 min in advance");
            timeselecter.getMenu().add(Menu.NONE, 20, Menu.NONE, "20 min in advance");
            timeselecter.getMenu().add(Menu.NONE, 25, Menu.NONE, "25 min in advance");
            timeselecter.getMenu().add(Menu.NONE, 30, Menu.NONE, "30 min in advance");
            // We set a listener for the menu items.
            //First we get the latest calendar and info
            final String i = info;
            final Calendar c = calendar;

            timeselecter.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    SharedPreferences.Editor prefsEditor = getActivity().getSharedPreferences("Time", getActivity().MODE_PRIVATE).edit();
                    prefsEditor.putInt(i , menuItem.getItemId()); // Because the ID corresponds to the time in seconds
                    prefsEditor.apply();
                    scheduleClient.setAlarmForNotification(c, i);
                    return true;
                }

            });
            // Finally, we show our freshly created menu.

            timeselecter.show();
            //scheduleClient.setAlarmForNotification(calendar, info);
            Log.d(msg, "StateChanged - if true: " + state + ", " + position);
        }else{
            // Cancel notification

            SharedPreferences prefsEditor = getActivity().getSharedPreferences("Events", getActivity().MODE_PRIVATE);
            int toCancel = prefsEditor.getInt(info, -1);
            Log.i(msg, "Prefs gotten: " + "from: " + info + ", which gives: " + toCancel);
            if((toCancel != -1)) {
                NotificationManager mNM = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
                mNM.cancel(toCancel);
            }

            Log.d(msg, "StateChanged - if false: " + state + ", " + position);
        }

    }

    public boolean onMenuItemClick(MenuItem item) {
        if (true)
      return true;
        else return false;
    }
    public void setCommunicator(Communicator communicator) { this.communicator = communicator; }




    public interface Communicator {
        public void respond(Bundle bundle);
    }

    @Override
    public void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if (scheduleClient != null){
            scheduleClient.doUnbindService();
        }
        super.onStop();

    }



}
