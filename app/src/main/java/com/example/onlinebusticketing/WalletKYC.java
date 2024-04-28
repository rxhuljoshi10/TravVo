package com.example.onlinebusticketing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WalletKYC extends AppCompatActivity {

    CheckBox checkBox;
    Button nextBtn;
    EditText walletName, walletAadhaarNum;
    String name;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_kyc);

        checkBox = findViewById(R.id.checkboxTerms);
        nextBtn = findViewById(R.id.walletNextBtn);
        walletName = findViewById(R.id.walletName);
        walletAadhaarNum = findViewById(R.id.walletAadhaarNum);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_key_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        name = sharedPreferences.getString("name",null);
        if(name!=null){
            walletName.setText(name);
        }


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                nextBtn.setEnabled(isChecked);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = walletName.getText().toString();
                if(validName(userName) && validAadhaar(walletAadhaarNum.getText().toString())){
                    if(name == null){
                        saveData(userName);
                    }
                    markWalletAsActivated();
                    startActivity(new Intent(WalletKYC.this, RechargePage.class));
                    Toast.makeText(WalletKYC.this, "Wallet Activated", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent();
//                    intent.putExtra("resultKey", "activated");
//                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                else{
                    Toast.makeText(WalletKYC.this, "Enter Valid Details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveData(String userName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", userName).apply();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child(userId).child("name").setValue(userName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void markWalletAsActivated(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userId).child("wallet").setValue(true);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("wallet",true);
        editor.apply();
    }

    private boolean validAadhaar(String aadhaar) {
        if(!aadhaar.isEmpty()){
            return aadhaar.length() == 12;
        }
        return false;
    }

    private boolean validName(String name) {
        return !name.isEmpty();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
        return super.onOptionsItemSelected(item);
    }
}