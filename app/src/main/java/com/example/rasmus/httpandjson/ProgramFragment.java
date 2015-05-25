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
        EventAdapter eventAdapter = new EventAdapter(getActivity(), R.layout.listview_item_row, events);
        programList.setAdapter(eventAdapter);

        programList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(msg, "Position: " + position);
                communicator.respond(position);
            }
        });
    }

    public void setCommunicator(Communicator communicator) { this.communicator = communicator; }



    public interface Communicator {
        public void respond(int position);
    }

}
