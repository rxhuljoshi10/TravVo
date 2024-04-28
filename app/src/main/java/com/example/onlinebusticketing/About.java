package com.example.onlinebusticketing;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {
    TextView featuresView, missionView, contactView, developersView;
    ImageView featuresIcon, missionIcon, contactIcon, developersIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        featuresView = findViewById(R.id.featuresView);
        featuresIcon = findViewById(R.id.featuresIcon);
        missionView = findViewById(R.id.missionView);
        missionIcon = findViewById(R.id.missionIcon);
        contactView = findViewById(R.id.contactView);
        contactIcon = findViewById(R.id.contactIcon);
        developersView = findViewById(R.id.developersView);
        developersIcon = findViewById(R.id.developersIcon);

    }

    public void previous(View v){
        getOnBackPressedDispatcher().onBackPressed();
        finish();
    }

    private void toggle(TextView textView, ImageView icon) {
        String tag = icon.getTag().toString();
        if(tag.equals("down")){
            textView.setVisibility(View.VISIBLE);
            icon.setImageResource(R.drawable.ic_key_up);
            icon.setTag("up");
        }
        else {
            textView.setVisibility(View.GONE);
            icon.setImageResource(R.drawable.ic_key_down);
            icon.setTag("down");
        }
    }

    public void toggleFeatures(View v){
        toggle(featuresView, featuresIcon);
    }

    public void toggleMissionStatement(View view) {
        toggle(missionView, missionIcon);
    }

    public void toggleContact(View view) {
        toggle(contactView, contactIcon);
    }

    public void toggleDevelopers(View view) {
        toggle(developersView, developersIcon);
    }
}