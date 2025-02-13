package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class allBusStops extends AppCompatActivity implements InputActivityAdapter.OnItemClickListener{

    InputActivityAdapter inputActivityAdapter;
    EditText inputField;
    RecyclerView stopsView;
    ImageView imgClear;
    ArrayList<String> stopNames = new ArrayList<>();
    List<Location> stopsLocationList = new ArrayList<>();
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    String entry;
    int position;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences cookies = getSharedPreferences("Cookies", MODE_PRIVATE);
        String homePage = cookies.getString("homePage", "Home");

        if (homePage.equals("MetroHome")) {
            setTheme(R.style.Theme_MetroUI);
        } else {
            setTheme(R.style.Theme_BusUI);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_bus_stops);

        Intent intent = getIntent();
        entry = intent.getStringExtra("Entry");
        position = intent.getIntExtra("position", -1);
//        stopNames = intent.getStringArrayListExtra("stopNames");
        stopNames = databaseHelper.getStopNames();


        stopsView = findViewById(R.id.stopsView);
        imgClear = findViewById(R.id.imgClear);
        inputField = findViewById(R.id.inputField);
        inputField.requestFocus();
        inputField.addTextChangedListener(new CustomTextWatcher());

        stopsLocationList = databaseHelper.getStopsLocationList();

        inputActivityAdapter = new InputActivityAdapter(this, stopNames, R.layout.item_stop_name_list);
        stopsView.setAdapter(inputActivityAdapter);

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputField.setText("");
            }
        });
    }



    @Override
    public void onItemClick(String selectedItem){
        if(entry.equals("Select")){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("userInput", selectedItem);
            resultIntent.putExtra("position", position);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else if (entry.equals("New")) {
            Intent intent = new Intent(allBusStops.this, AddPlace.class);
            intent.putExtra("stop", selectedItem);
            startActivityForResult(intent, 1);
        } else {
            ArrayList<String> stopBuses = databaseHelper.getStopBuses(selectedItem);
            removeOddIndices(stopBuses);
            Intent intent = new Intent(allBusStops.this, EligibleBusList.class);
            intent.putExtra("source", "");
            intent.putExtra("destination", "");
            intent.putStringArrayListExtra("eligibleBuses", stopBuses);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String stop = data.getStringExtra("stop");
            String title = data.getStringExtra("title");
            Intent resultIntent = new Intent();
            resultIntent.putExtra("userInput", stop);
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("position", -2);
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
                imgClear.setVisibility(View.VISIBLE);
            }
            else{
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