package com.example.onlinebusticketing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import java.util.List;


public class SplashScreen extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(SplashScreen.this);
    private static int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
        boolean isFirstLaunch = sharedPreferences.getBoolean("firstLaunch", true);

//        LocationHelper locationHelper = new LocationHelper(this);
//        locationHelper.startFetchingLocation();

        if (isFirstLaunch) {
            loadDataInBg();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstLaunch", false);
            editor.apply();
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(isFirstLaunch){
                    intent = new Intent(SplashScreen.this, LanguageSelection.class);
                    intent.putExtra("entry", "firstLaunch");
                }
                else {
                    intent = new Intent(SplashScreen.this, LoginPage.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);

    }

    private void loadDataInBg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }).start();
    }
    private void loadData() {
        List<List<String>> busRoutes = ExcelReader.readCsvData(SplashScreen.this, "Bus_Routes_1_modified.csv","Bus_Routes_2_modified.csv");
        databaseHelper.insertIntoBusRoutes(busRoutes);
    }
}