package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class PaymentMethods extends AppCompatActivity {
    LinearLayout pMethod1, pMethod2, pMethod3;
    TextView amountView;
    Button payBtn;
    Toolbar toolbar;
    Drawable drawable;

    @Override
    @SuppressLint("UseCompatLoadingForDrawables")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        Intent intent = getIntent();
        String amount = intent.getStringExtra("amount");

        pMethod1 = findViewById(R.id.payMethod_UPI);
        pMethod1.setBackground(getDrawable(R.drawable.borders_highlighted));
        pMethod2 = findViewById(R.id.payMethod_netBanking);
        pMethod3 = findViewById(R.id.payMethod_Card);
        payBtn = findViewById(R.id.payBtn);
        amountView = findViewById(R.id.amountView);
        amountView.setText("₹"+amount);

        drawable = ContextCompat.getDrawable(this, R.drawable.borders_highlighted);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add ₹" + amount);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_key_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        changeTheme();

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtil.updateWalletPrice(PaymentMethods.this, Float.parseFloat(amount) + MyUtil.getWalletBalance(PaymentMethods.this));
                startActivity(new Intent(PaymentMethods.this, SuccessPayment.class));
            }
        });

        pMethod1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pMethod1.setBackground(drawable);
                pMethod2.setBackground(getDrawable(R.drawable.borders));
                pMethod3.setBackground(getDrawable(R.drawable.borders));
            }
        });

        pMethod2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pMethod2.setBackground(drawable);
                pMethod1.setBackground(getDrawable(R.drawable.borders));
                pMethod3.setBackground(getDrawable(R.drawable.borders));
            }
        });

        pMethod3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pMethod3.setBackground(drawable);
                pMethod1.setBackground(getDrawable(R.drawable.borders));
                pMethod2.setBackground(getDrawable(R.drawable.borders));
            }
        });
    }

    public void changeTheme(){
        SharedPreferences cookies = getSharedPreferences("Cookies", Context.MODE_PRIVATE);
        String activityName = cookies.getString("homePage", "Home");
        if (activityName.equals("MetroHome")){
            int color = ContextCompat.getColor(this, R.color.primaryColorMetro);

            payBtn.setBackgroundColor(color);
            toolbar.setBackgroundColor(color);
            drawable = ContextCompat.getDrawable(this, R.drawable.borders_highlighted_metro);
            pMethod1.setBackground(drawable);
            Window window = getWindow();
            window.setStatusBarColor(color);

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
