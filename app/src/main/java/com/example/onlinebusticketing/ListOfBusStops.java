package com.example.onlinebusticketing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class ListOfBusStops extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView bus_list_view;
    ArrayList<String> stops_list = new ArrayList<>();

    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stops_list);

        bus_list_view = findViewById(R.id.bus_list_view);

        Intent intent = getIntent();
        String busNumber = intent.getStringExtra("busNumber");
        String source = intent.getStringExtra("Source");
        String destination = intent.getStringExtra("Destination");
        String filterBusNumber = busNumber.substring(0, busNumber.indexOf('-'));

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(filterBusNumber);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_bus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stops_list = databaseHelper.getStops(busNumber);
        int sourceNum = databaseHelper.getSequenceNumber(source, busNumber);
        int destinNum = databaseHelper.getSequenceNumber(destination, busNumber);
        BusStops_Adapter adapter = new BusStops_Adapter(stops_list, sourceNum, destinNum);
        bus_list_view.setAdapter(adapter);
    }
}