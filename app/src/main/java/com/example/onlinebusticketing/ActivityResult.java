package com.example.onlinebusticketing;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityResult extends ActivityResultContract<Intent, String> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Intent input) {
        return input;
    }

    @Override
    public String parseResult(int resultCode, @Nullable Intent intent) {
        if (resultCode == android.app.Activity.RESULT_OK && intent != null) {
            return intent.getStringExtra("resultKey");
        }
        return null;
    }
}

