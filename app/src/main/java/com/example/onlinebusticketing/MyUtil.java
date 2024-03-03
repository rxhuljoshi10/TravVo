package com.example.onlinebusticketing;

import android.content.Context;
import android.content.SharedPreferences;

public class MyUtil {
    static public boolean isWalletActivated(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("wallet",false);
    }
}
