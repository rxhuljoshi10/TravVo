package com.example.onlinebusticketing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedPlaces extends AppCompatActivity implements SavedPlacesAdapter.OnItemClickListener, SavedPlacesAdapter.OnDelViewClickListener {
    List<Map.Entry<String, String>> savedPlacesList = new ArrayList<>();
    RecyclerView savedPlacesView;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    String userId, entry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_places);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        savedPlacesView = findViewById(R.id.savedPlacesView);
        savedPlacesList = databaseHelper.getSavedPlacesList(userId);

        Intent intent= getIntent();
        entry = intent.getStringExtra("entry");

        SavedPlacesAdapter savedPlacesAdapter = new SavedPlacesAdapter(this,this, savedPlacesList);
        savedPlacesView.setAdapter(savedPlacesAdapter);
    }

    public void previous(View v){
        getOnBackPressedDispatcher().onBackPressed();
        finish();
    }

    public void addNewSavedPlace(View v){
        Intent intent = new Intent(SavedPlaces.this, allBusStops.class);
        intent.putExtra("Entry", "New");
        intent.putExtra("position", -2);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onItemClick(Map.Entry<String, String> selectedItem, int position) {
        if(selectedItem.getValue() == null){
            Intent intent = new Intent(SavedPlaces.this, allBusStops.class);
            intent.putExtra("Entry", "Select");
            intent.putExtra("position", position);
            startActivityForResult(intent, 1);
        }
        else {
            if(entry.equals("Select")) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("userInput", selectedItem.getValue());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            String stop = data.getStringExtra("userInput");
            int position = data.getIntExtra("position",-1);
            if(position != -1){
                if(position == 0) {
                    Map.Entry<String, String> entry = savedPlacesList.get(position);
                    entry = new AbstractMap.SimpleEntry<>("Home", stop);
                    savedPlacesList.set(position, entry);
                } else if (position == 1) {
                    Map.Entry<String, String> entry = savedPlacesList.get(position);
                    entry = new AbstractMap.SimpleEntry<>("Work", stop);
                    savedPlacesList.set(position, entry);
                }

                else {
                    String title = data.getStringExtra("title");
                    Map.Entry<String, String> entry;
                    entry = new AbstractMap.SimpleEntry<>(title, stop);
                    savedPlacesList.add(entry);
                }

                SavedPlacesAdapter savedPlacesAdapter = new SavedPlacesAdapter(this,this, savedPlacesList);
                savedPlacesView.setAdapter(savedPlacesAdapter);

                saveData();
            }
        }
    }

    @Override
    public void onDelViewClick(int position) {
        if (position == 0){
            Map.Entry<String, String> entry = savedPlacesList.get(position);
            entry = new AbstractMap.SimpleEntry<>("Add Home", null);
            savedPlacesList.set(position, entry);
        } else if (position == 1) {
            Map.Entry<String, String> entry = savedPlacesList.get(position);
            entry = new AbstractMap.SimpleEntry<>("Add Work", null);
            savedPlacesList.set(position, entry);
        }
        else {
            savedPlacesList.remove(position);
        }
        SavedPlacesAdapter savedPlacesAdapter = new SavedPlacesAdapter(this,this, savedPlacesList);
        savedPlacesView.setAdapter(savedPlacesAdapter);
        saveData();
    }

    private void saveData() {
        databaseHelper.storeSavedPlacesList(savedPlacesList, userId);
        Map<String, Map.Entry<String, String>> dataMap = new HashMap<>();

        for (int i = 0; i < savedPlacesList.size(); i++) {
            dataMap.put("place_" + i, savedPlacesList.get(i));
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Users").child(userId);
        databaseReference.child("saved_places").setValue(dataMap);
    }
}