package com.example.onlinebusticketing;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class BookTickets extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    List<String> bus_stops = new ArrayList<>();

    AutoCompleteTextView sourceEntry, destinationEntry;
    Button proceedBtn;
    ListView historyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_tickets);

        sourceEntry = findViewById(R.id.source);
        destinationEntry = findViewById(R.id.destination);
        historyView = findViewById(R.id.historyView);
        proceedBtn = findViewById(R.id.proceedBtn);
        proceedBtn.setEnabled(false);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        bus_stops = databaseHelper.getBusStops();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bus_stops);
        sourceEntry.setAdapter(adapter);
        destinationEntry.setAdapter(adapter);

        sourceEntry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = (String) parent.getItemAtPosition(position);
                if(valid_stop_check()){
                    proceedBtn.setEnabled(true);
                }
                destinationEntry.requestFocus();
            }
        });

        destinationEntry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = (String) parent.getItemAtPosition(position);
                if(valid_stop_check()){
                    proceedBtn.setEnabled(true);
                }
                valid_stop_check();
            }
        });

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid_stop_check()) {
                    databaseHelper.insertSearchHistory(FirebaseAuth.getInstance().getCurrentUser().getUid(),sourceEntry.getText().toString(), destinationEntry.getText().toString());
                    Intent intent = new Intent(BookTickets.this, Ticket_Summary.class);
                    String source = sourceEntry.getText().toString();
                    String destination = destinationEntry.getText().toString();
                    ArrayList<String> eligibleBuses = databaseHelper.getEligibleBuses(source, destination);
                    if(eligibleBuses.size() != 0){
                        intent.putExtra("source",source);
                        intent.putExtra("destination",destination);
                        intent.putStringArrayListExtra("eligibleBuses", eligibleBuses);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(BookTickets.this, "No bus found for this route..!!", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(BookTickets.this, "Invalid Bus Stop",Toast.LENGTH_SHORT).show();
                }
            }
        });


        ImageView swap = findViewById(R.id.imageView);
        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapInputEntry();
            }
        });


        List<String> lastSearchHistory = databaseHelper.getLastSearchHistory(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ArrayAdapter<String> historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lastSearchHistory);
        historyView.setAdapter(historyAdapter);

        historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedHistory = (String) parent.getItemAtPosition(position);
                String[] historyParts = selectedHistory.split(" - ");
                if (historyParts.length == 2) {
                    sourceEntry.setText(historyParts[0]);
                    destinationEntry.setText(historyParts[1]);
                    proceedBtn.setEnabled(true);
                }
            }
        });
    }

    private Boolean valid_stop_check() {
        String source = sourceEntry.getText().toString();
        String destination = destinationEntry.getText().toString();
        return bus_stops.contains(source) && bus_stops.contains(destination);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void swapInputEntry(){
        String sourceText = sourceEntry.getText().toString();
        String destinationText = destinationEntry.getText().toString();

        destinationEntry.setText(sourceText);
        sourceEntry.setText(destinationText);
    }
}