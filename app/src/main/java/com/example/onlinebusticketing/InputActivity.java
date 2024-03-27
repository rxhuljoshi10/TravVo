package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InputActivity extends AppCompatActivity implements InputActivityAdapter.OnItemClickListener{

    InputActivityAdapter inputActivityAdapter;
    EditText inputField;
    RecyclerView stopsView;
    ImageView imgLocation, imgClear;
    ArrayList<String> stopNames = new ArrayList<>();
    List<Location> stopsLocationList = new ArrayList<>();
    LocationHelper locationHelper = new LocationHelper(this);
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    String entry;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        Intent intent = getIntent();
        entry = intent.getStringExtra("entry");
        stopNames = intent.getStringArrayListExtra("stopNames");

        stopsView = findViewById(R.id.stopsView);
        imgLocation = findViewById(R.id.imgLocation);
        imgClear = findViewById(R.id.imgClear);
        inputField = findViewById(R.id.inputField);
        inputField.requestFocus();
        inputField.setHint(entry);
        inputField.addTextChangedListener(new CustomTextWatcher());

        stopsLocationList = databaseHelper.getStopsLocationList();

        inputActivityAdapter = new InputActivityAdapter(this, stopNames);
        stopsView.setAdapter(inputActivityAdapter);

        imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location currentLocation = locationHelper.getCurrentLocation();
                if (currentLocation != null) {
                    int nearestStopLocationIndex = findNearestBusStop(currentLocation.getLatitude(), currentLocation.getLongitude(), stopsLocationList);
                    String nearestBusStop = stopNames.get(nearestStopLocationIndex);
                    inputField.setText(nearestBusStop);
                }
            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputField.setText("");
            }
        });
    }

    private static int findNearestBusStop(double currentLat, double currentLon, List<Location> locations) {
        int nearestLocationIndex = -1;
        double minDistance = Double.MAX_VALUE;

        for (int i=0; i < locations.size(); i++) {
            Location location = locations.get(i);
            double distance = haversine(currentLat, currentLon, location.getLatitude(), location.getLongitude());

            if (distance < minDistance) {
                minDistance = distance;
                nearestLocationIndex = i;
            }
        }
        return nearestLocationIndex;
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    @Override
    public void onItemClick(String selectedItem){
        if(entry.equals("Enter Stop Name")){
            ArrayList<String> stopBuses = databaseHelper.getStopBuses(selectedItem);
            removeOddIndices(stopBuses);
            Intent intent = new Intent(InputActivity.this, EligibleBusList.class);
            intent.putExtra("source","");
            intent.putExtra("destination","");
            intent.putStringArrayListExtra("eligibleBuses",stopBuses);
            startActivity(intent);
        }
        else{
            Intent resultIntent = new Intent();
            resultIntent.putExtra("userInput", selectedItem);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    public void goBack(View view){
        getOnBackPressedDispatcher().onBackPressed();
    }

    private class CustomTextWatcher implements android.text.TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            inputActivityAdapter.filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!inputField.getText().toString().isEmpty()){
                imgLocation.setVisibility(View.GONE);
                imgClear.setVisibility(View.VISIBLE);
            }
            else{
                imgLocation.setVisibility(View.VISIBLE);
                imgClear.setVisibility(View.GONE);
            }
        }
    }

    private void removeOddIndices(ArrayList<String> list) {
        Iterator<String> iterator = list.iterator();
        int index = 0;

        while (iterator.hasNext()) {
            iterator.next();

            // Remove elements at odd positions
            if (index % 2 != 0) {
                iterator.remove();
            }
            index++;
        }
    }
}