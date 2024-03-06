package com.example.onlinebusticketing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RechargePage extends AppCompatActivity {
    TextView walletBalanceView, warningView, nextBtn;
    EditText inputAmount;
    int RechargeAmount = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_page);

        inputAmount = findViewById(R.id.inputAmount);
        nextBtn = findViewById(R.id.nextBtn);
        walletBalanceView = findViewById(R.id.walletBalanceView);
        warningView = findViewById(R.id.warningView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        float walletBalance = intent.getFloatExtra("walletBalance",0.0F);
        walletBalanceView.setText(String.valueOf(walletBalance));

        inputAmount.addTextChangedListener(new CustomTextWatcher());
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = inputAmount.getText().toString();
                updateWalletPrice(Float.parseFloat(amount) + walletBalance);
                Intent intent1 = new Intent(RechargePage.this, PaymentMethods.class);
                intent1.putExtra("amount", amount);
                startActivity(intent1);
                Toast.makeText(RechargePage.this, "₹"+amount+" Added in your wallet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWalletPrice(float amount) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userId).child("walletBalance").setValue(amount);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("walletBalance", amount);
        editor.apply();
    }

    public void setAmount(View view){
        String cardText = "";

        if (view.getId() == R.id.rechargeAmount1) {
            cardText = "100";
        } else if (view.getId() == R.id.rechargeAmount2) {
            cardText = "300";
        } else if (view.getId() == R.id.rechargeAmount3) {
            cardText = "500";
        }
        RechargeAmount = Integer.parseInt(cardText);
        inputAmount.setText(cardText);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private class CustomTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                if (s.length() != 0) {
                    int enteredAmount = Integer.parseInt(s.toString());
                    if (enteredAmount < 10) {
                        warningView.setText("Amount should be greater than 10");
                        warningView.setVisibility(View.VISIBLE);
                        nextBtn.setEnabled(false);
                    } else if (enteredAmount > 5000) {
                        warningView.setText("Amount should be less than 5000");
                        warningView.setVisibility(View.VISIBLE);
                        nextBtn.setEnabled(false);
                    }
                    if (enteredAmount >= 10 && enteredAmount <= 5000) {
                        warningView.setVisibility(View.GONE);
                        nextBtn.setEnabled(true);
                    }
                } else {
                    warningView.setVisibility(View.GONE);
                }
            }
            catch (Exception e){}
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}