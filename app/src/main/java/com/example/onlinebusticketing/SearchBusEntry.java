package com.example.onlinebusticketing;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;


public class SearchBusEntry extends AppCompatActivity {
    EditText busInputField;
    RecyclerView searchBusRecyclerView;
    searchBusAdapter adapter;

    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bus_entry);

        searchBusRecyclerView = findViewById(R.id.searchBusRecyclerView);
        busInputField = findViewById(R.id.busInputField);
        busInputField.requestFocus();

        ArrayList<String> dataList = databaseHelper.getAllBusNumbers();
        removeOddIndices(dataList);

        adapter = new searchBusAdapter(dataList, databaseHelper);
        searchBusRecyclerView.setAdapter(adapter);

        busInputField.addTextChangedListener(new CustomTextWatcher());
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

    public void goBack(View view){
        getOnBackPressedDispatcher().onBackPressed();
    }

    private class CustomTextWatcher implements android.text.TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            adapter.filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}