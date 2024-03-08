package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PaymentMethods extends AppCompatActivity {
    LinearLayout pMethod1, pMethod2, pMethod3;
    TextView amountView;
    Button payBtn;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add ₹" + amount);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                pMethod1.setBackground(getDrawable(R.drawable.borders_highlighted));
                pMethod2.setBackground(getDrawable(R.drawable.borders));
                pMethod3.setBackground(getDrawable(R.drawable.borders));
            }
        });

        pMethod2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pMethod2.setBackground(getDrawable(R.drawable.borders_highlighted));
                pMethod1.setBackground(getDrawable(R.drawable.borders));
                pMethod3.setBackground(getDrawable(R.drawable.borders));
            }
        });

        pMethod3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pMethod3.setBackground(getDrawable(R.drawable.borders_highlighted));
                pMethod1.setBackground(getDrawable(R.drawable.borders));
                pMethod2.setBackground(getDrawable(R.drawable.borders));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
