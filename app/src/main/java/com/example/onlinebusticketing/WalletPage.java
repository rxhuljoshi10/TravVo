package com.example.onlinebusticketing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WalletPage extends AppCompatActivity {

    TextView rechargeBtn, balanceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_page);

        rechargeBtn = findViewById(R.id.rechargeBtn);
        balanceView = findViewById(R.id.balanceView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_key_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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