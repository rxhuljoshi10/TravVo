package com.example.onlinebusticketing;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PaymentMethods extends AppCompatActivity {
    LinearLayout payMethod_Wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

//        payMethod_Wallet = findViewById(R.id.payMethod_Wallet);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int amount = intent.getIntExtra("amount", 0);
        toolbar.setTitle("Add ₹" + amount);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
