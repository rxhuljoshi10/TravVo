package com.example.onlinebusticketing;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class MyUtil {
    static public boolean isWalletActivated(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("wallet",false);
    }
    public static TextView getHeaderTextView(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0); // Adjust the index if you have multiple headers
        return headerView.findViewById(R.id.nav_header_view);
    }

    public static float getWalletBalance(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        return sharedPreferences.getFloat("walletBalance",0.0F);
    }
}
