package com.example.onlinebusticketing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class CabHome extends AppCompatActivity {

    ImageView swipeView;
    GestureDetector gestureDetector;
    SharedPreferences cookies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_cab_home);

        swipeView = findViewById(R.id.swipeView);

        cookies = getSharedPreferences("Cookies", Context.MODE_PRIVATE);

        gestureDetector = new GestureDetector(this, new CabHome.SwipeGestureListener());
        swipeView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 20;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX < 0) {
                    Intent intent = new Intent(CabHome.this, Home.class);
                    SharedPreferences.Editor editor = cookies.edit();
                    editor.putString("homePage", "Home").apply();
                    recreate();
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                }
                else{
                    Intent intent = new Intent(CabHome.this, MetroHome.class);
                    SharedPreferences.Editor editor = cookies.edit();
                    editor.putString("homePage", "MetroHome").apply();
                    recreate();
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    return true;
                }

            }
            return false;
        }
    }
}