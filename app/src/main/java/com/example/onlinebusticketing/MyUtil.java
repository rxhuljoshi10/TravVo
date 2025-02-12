package com.example.onlinebusticketing;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public static void updateWalletPrice(Context context, float amount) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userId).child("walletBalance").setValue(amount);
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("walletBalance", amount);
        editor.apply();
    }
    private void sendSms(Context context) {
        String phone = "+919561775411";
        String message = "hello there";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "SMS failed to send", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void loadgif(Context context){
//        Glide.with(this)
//                .asGif()
//                .load(R.raw.swipe2)
//                .placeholder(R.drawable.ic_launcher_background)
//                .listener(new RequestListener<GifDrawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
//                        if (resource != null) {
//                            resource.start();
//                        }
//                        return false;
//                    }
//                })
//                .into(viewDraggable);
    }
}
