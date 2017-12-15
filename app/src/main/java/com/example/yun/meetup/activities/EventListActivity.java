package com.example.yun.meetup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yun.meetup.R;
import com.example.yun.meetup.adapters.EventListViewAdapter;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;
import com.example.yun.meetup.models.UserInfo;
import com.example.yun.meetup.requests.EventListRequest;

import java.util.Arrays;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private ListView listViewEvents;
    private ConstraintLayout constraintLayoutEventListLoading;
    private List<Event> listEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        listViewEvents = (ListView) findViewById(R.id.listViewEvents);
        constraintLayoutEventListLoading = (ConstraintLayout) findViewById(R.id.constraintLayoutEventListLoading);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Event List");

        /*Button that calls the Create an Event Activity*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EventListActivity.this, CreateEventActivity.class);
                startActivity(intent);

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(EventListActivity.this, "Selected event: " + listEvents.get(i).getTitle(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(EventListActivity.this, EventDetailsActivity.class);
                startActivity(intent);
            }
        });

        EventListRequest eventListRequest = new EventListRequest();

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        eventListRequest.setHost_id(sharedPreferences.getString("id", null));

        new EventListTask().execute(eventListRequest);


    }

    private class EventListTask extends AsyncTask<EventListRequest, Void, APIResult>{

        @Override
        protected APIResult doInBackground(EventListRequest... eventListRequests) {
            NetworkManager networkManager = new NetworkManager();
            return networkManager.getHostedEvents(eventListRequests[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            if (apiResult.isResultSuccess()){

                listEvents = (List<Event>) apiResult.getResultEntity();

                EventListViewAdapter adapter = new EventListViewAdapter(listEvents, getApplicationContext());

                listViewEvents.setAdapter(adapter);

                constraintLayoutEventListLoading.setVisibility(View.GONE);

            }
        }
    }

}
