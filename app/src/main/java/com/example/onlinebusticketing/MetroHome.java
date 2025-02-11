package com.example.onlinebusticketing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
public class MetroHome extends AppCompatActivity {
    ImageView swipeView;

    private GestureDetector gestureDetector;
    SharedPreferences cookies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metro_home);

        swipeView = findViewById(R.id.swipeView);

        cookies = getSharedPreferences("Cookies", Context.MODE_PRIVATE);

        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
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
                    Intent intent = new Intent(MetroHome.this, Home.class);
                    SharedPreferences.Editor editor = cookies.edit();
                    editor.putString("homePage", "Home");
                    editor.apply();

                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                }
            }
            return false;
        }
    }
}