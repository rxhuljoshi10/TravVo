package com.example.onlinebusticketing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PaymentMethods extends AppCompatActivity {
    LinearLayout payMethod_Wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        payMethod_Wallet = findViewById(R.id.payMethod_Wallet);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int amount = intent.getIntExtra("amount", 0);
        toolbar.setTitle("Pay ₹" + amount);

        payMethod_Wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(isWalletActivated()){
                    Toast.makeText(PaymentMethods.this, "Payment Done", Toast.LENGTH_SHORT).show();
                }
                else{
                    FragmentWalletActivation activateWallet = new FragmentWalletActivation();
                    activateWallet.show(getSupportFragmentManager(), activateWallet.getTag());
                }
//                walletActivated();
            }
        });
    }

    private boolean isWalletActivated() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("wallet",false);
    }

//    private void walletActivated() {
//        userWallet(new WalletCallback() {
//            @Override
//            public void onWalletValue(boolean walletValue) {
//                if (!walletValue) {
//                    ActivateWallet activateWallet = new ActivateWallet();
//                    activateWallet.show(getSupportFragmentManager(), activateWallet.getTag());
//                }
//            }
//        });
//    }
//
//    private void userWallet(WalletCallback callback) {
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
//        databaseReference.child("wallet").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                boolean walletValue = snapshot.getValue(Boolean.class);
//                callback.onWalletValue(walletValue);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle any errors
//            }
//        });
//    }

    private void sendSms() {
        String phone = "+919561775411";
        String message = "hello there";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed to send", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    // WalletCallback interface
    public interface WalletCallback {
        void onWalletValue(boolean walletValue);
    }
}
