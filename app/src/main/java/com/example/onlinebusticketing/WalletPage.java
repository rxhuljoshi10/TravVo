package com.example.onlinebusticketing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class WalletPage extends AppCompatActivity {

    TextView rechargeBtn, balanceView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_page);

        rechargeBtn = findViewById(R.id.rechargeBtn);
        balanceView = findViewById(R.id.balanceView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_key_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeTheme();

//        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
//        float walletBalance = sharedPreferences.getFloat("walletBalance",(float) 0.00);
//        balanceView.setText("₹"+walletBalance);

//        rechargeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(WalletPage.this, RechargePage.class);
//                intent.putExtra("walletBalance", walletBalance);
//                startActivity(intent);
//            }
//        });

    }

    public void changeTheme(){
        SharedPreferences cookies = getSharedPreferences("Cookies", Context.MODE_PRIVATE);
        String activityName = cookies.getString("homePage", "Home");
        if (activityName.equals("MetroHome")){
            int color = ContextCompat.getColor(this, R.color.primaryColorMetro);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bordered_backgroud_metro);
            rechargeBtn.setTextColor(color);
            rechargeBtn.setBackground(drawable);
            toolbar.setBackgroundColor(color);
            Window window = getWindow();
            window.setStatusBarColor(color);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        float walletBalance = sharedPreferences.getFloat("walletBalance",(float) 0.00);
        balanceView.setText("₹"+walletBalance);

        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletPage.this, RechargePage.class);
                intent.putExtra("walletBalance", walletBalance);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}