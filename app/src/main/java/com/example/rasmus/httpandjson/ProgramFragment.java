package com.example.rasmus.httpandjson;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.rasmus.httpandjson.Adapter.EventAdapter;
import com.example.rasmus.httpandjson.model.Event;
import com.example.rasmus.httpandjson.util.ProgramService;

import java.util.ArrayList;

public class ProgramFragment extends Fragment {
    String msg = "Rasmus Logging";

    ArrayAdapter<Event> eventAdapter = null;
    ProgramService ProgramService;



    Communicator communicator;
    ListView programList;


    public ProgramFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.program_fragment, container, false);

        programList = (ListView) view.findViewById(R.id.programList);


        return view;
    }

    public void changeData(ArrayList<Event> events){
        final EventAdapter eventAdapter = new EventAdapter(getActivity(), R.layout.listview_item_row, events);
        programList.setAdapter(eventAdapter);

        programList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(msg, "Position: " + position);

              Event e = eventAdapter.getItem(position);

             Log.d(msg, "Latitude: "+ e.getLatitude());

             Bundle b = new Bundle();

             b.putString("latitude",e.getLatitude());
             b.putString("longitude",e.getLongitude());
             b.putString("description",e.getDescription());
             b.putString("date",e.getDate());
             b.putString("name",e.getName());
             b.putString("id",""+e.getId());
             b.putString("time",e.getTime());
             b.putString("type",e.getType());

            communicator.respond(b);
            }
        });
    }

    public void setCommunicator(Communicator communicator) { this.communicator = communicator; }



    public interface Communicator {
        public void respond(Bundle bundle);
    }

}
