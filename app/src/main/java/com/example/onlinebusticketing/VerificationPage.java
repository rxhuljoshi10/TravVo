package com.example.onlinebusticketing;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class VerificationPage extends AppCompatActivity {
    String verificationID, phone;
    private EditText e1, e2, e3, e4, e5, e6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_verify_otp);

        e1 = findViewById(R.id.editText1);
        e2 = findViewById(R.id.editText2);
        e3 = findViewById(R.id.editText3);
        e4 = findViewById(R.id.editText4);
        e5 = findViewById(R.id.editText5);
        e6 = findViewById(R.id.editText6);

        setEditTextWatcher(e1, e2);
        setEditTextWatcher(e2, e3);
        setEditTextWatcher(e3, e4);
        setEditTextWatcher(e4, e5);
        setEditTextWatcher(e5, e6);
        e1.requestFocus();

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone_number");

        TextView textView = findViewById(R.id.verify_label);
        String newText = textView.getText().toString() + phone;
        textView.setText(newText);

        Intent intent1 = getIntent();
        verificationID = intent1.getStringExtra("verificationID");



        CardView verify_otp = findViewById(R.id.verify_button);
        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = getOtpString();
                if(otp.isEmpty()){
                    Toast.makeText(VerificationPage.this, "OTP is invalid", Toast.LENGTH_SHORT).show();
                }
                else{
                    verifyCode(otp);
                    findViewById(R.id.pBar).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setEditTextWatcher(final EditText currentEditText, final EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() == 1) {
                    // Move focus to the next EditText
                    nextEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private String getOtpString() {
        return e1.getText().toString() +
                e2.getText().toString() +
                e3.getText().toString() +
                e4.getText().toString() +
                e5.getText().toString() +
                e6.getText().toString();
    }
    private void verifyCode(String Code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, Code);
        signInByCredentials(credential);
    }
    private void signInByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    checkUser();
                    Toast.makeText(VerificationPage.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerificationPage.this, Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else{
                    findViewById(R.id.pBar).setVisibility(View.INVISIBLE);
                    Toast.makeText(VerificationPage.this, "OTP is invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUser() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    fetchUserData(userId);
                }
                else{
                    initUser(userId, databaseReference);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void initUser(String userId, DatabaseReference databaseReference) {
        databaseReference.child(userId).child("phone").setValue(phone);
        databaseReference.child(userId).child("wallet").setValue(false);
        databaseReference.child(userId).child("walletBalance").setValue(0.0);

        writeDataIntoPreference(phone, false, (float) 0.0);
    }

    private void fetchUserData(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String phone = snapshot.child("phone").getValue(String.class);
                    boolean hasWallet = Boolean.TRUE.equals(snapshot.child("wallet").getValue(Boolean.class));
                    float walletBalance = snapshot.child("walletBalance").getValue(Integer.class);

                    writeDataIntoPreference(phone, hasWallet, walletBalance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void writeDataIntoPreference(String phone, boolean wallet, float walletBalance) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone",phone);
        editor.putBoolean("wallet",wallet);
        editor.putFloat("walletBalance", walletBalance);
        editor.clear();
        editor.apply();
    }
}