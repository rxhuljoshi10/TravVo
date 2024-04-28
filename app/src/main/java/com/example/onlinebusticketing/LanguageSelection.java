package com.example.onlinebusticketing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageSelection extends AppCompatActivity {

    private List<CardView> cardViews;
    private List<ImageView> checkImageViews;

    int index = 0;
    String entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        Intent intent = getIntent();
        entry = intent.getStringExtra("entry");


        cardViews = new ArrayList<>();
        cardViews.add(findViewById(R.id.lang1));
        cardViews.add(findViewById(R.id.lang2));
        cardViews.add(findViewById(R.id.lang3));
        cardViews.add(findViewById(R.id.lang4));
        cardViews.add(findViewById(R.id.lang5));
        cardViews.add(findViewById(R.id.lang6));

        checkImageViews = new ArrayList<>();
        checkImageViews.add(findViewById(R.id.imageView1));
        checkImageViews.add(findViewById(R.id.imageView2));
        checkImageViews.add(findViewById(R.id.imageView3));
        checkImageViews.add(findViewById(R.id.imageView4));
        checkImageViews.add(findViewById(R.id.imageView5));
        checkImageViews.add(findViewById(R.id.imageView6));

        clearUI();
        SharedPreferences sharedPreferences = getSharedPreferences("Cookies", Context.MODE_PRIVATE);
        index = sharedPreferences.getInt("langIndex",0);
        highlightSelectedLang(index);

        CardView cardView  = findViewById(R.id.continueBtn);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyLang();
            }
        });
    }

    public void onCardViewClick(View view) {
        CardView clickedCardView = (CardView) view;
        clearUI();
        index = cardViews.indexOf(clickedCardView);

        highlightSelectedLang(index);

    }

    private void highlightSelectedLang(int index) {
        cardViews.get(index).setOutlineSpotShadowColor(getResources().getColor(R.color.primaryColor));
        checkImageViews.get(index).setVisibility(View.VISIBLE);
    }

    private void clearUI() {
        for (CardView cardView : cardViews) {
            cardView.setOutlineSpotShadowColor(R.color.black);
        }

        for (ImageView imageView : checkImageViews) {
            imageView.setVisibility(View.GONE);
        }
    }

    public void applyLang(){
        setLocal(LanguageSelection.this, getLang(index));
        SharedPreferences sharedPreferences = getSharedPreferences("Cookies", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("langIndex", index).putString("appLang", getLang(index)).apply();
        if(entry.equals("firstLaunch")) {
            startActivity(new Intent(this, LoginPage.class));
        }
        else {
            startActivity(new Intent(this, Home.class));
        }
        finish();
    }

    private String getLang(int index) {
        if(index == 0){
            return "en";
        } else if (index == 1) {
            return "hi";
        } else if (index == 2) {
            return "mr";
        }
        else if (index == 3) {
            return "tam";
        }
        else if (index == 4) {
            return "urd";
        }
        else if (index == 5) {
            return "bn";
        } else{
            return "en";
        }
    }

    public void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public void previous(View v){
        getOnBackPressedDispatcher().onBackPressed();
        finish();
    }
}