package com.example.onlinebusticketing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Help extends AppCompatActivity {

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
        setContentView(R.layout.activity_help);
    }

    public void previous(View v){
        getOnBackPressedDispatcher().onBackPressed();
        finish();
    }

    public void startCall(View v){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:8421512989"));
        startActivity(intent);
    }
}