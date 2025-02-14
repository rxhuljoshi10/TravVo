package com.example.onlinebusticketing;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MetroMap extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_MetroUI);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metro_map);
    }

    public void previous(View v){
        getOnBackPressedDispatcher().onBackPressed();
        finish();
    }
}