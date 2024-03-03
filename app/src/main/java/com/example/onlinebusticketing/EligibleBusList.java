package com.example.onlinebusticketing;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EligibleBusList extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    RecyclerView eligibleBusView;
    ArrayList<String> eligibleBuses = new ArrayList<>();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligible_bus_list);

        eligibleBusView = findViewById(R.id.eligibleBusView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        String destination = intent.getStringExtra("destination");
        eligibleBuses = intent.getStringArrayListExtra("eligibleBuses");

        if(source.isEmpty()){
            toolbar.setTitle("Buses On Stop");
        }

        EligibleBusAdapter eligibleBusAdapter = new EligibleBusAdapter(eligibleBuses, source, destination, databaseHelper);
        eligibleBusView.setAdapter(eligibleBusAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}