package com.example.onlinebusticketing;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VerificationPage extends AppCompatActivity {
    String verificationID, phone;
    private EditText e1, e2, e3, e4, e5, e6;

    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_verification_page);

        e1 = findViewById(R.id.editText1);
        e2 = findViewById(R.id.editText2);
        e3 = findViewById(R.id.editText3);
        e4 = findViewById(R.id.editText4);
        e5 = findViewById(R.id.editText5);
        e6 = findViewById(R.id.editText6);

        setEditTextWatcher(e1, e2, null);
        setEditTextWatcher(e2, e3, e1);
        setEditTextWatcher(e3, e4, e2);
        setEditTextWatcher(e4, e5, e3);
        setEditTextWatcher(e5, e6, e4);
        setEditTextWatcher(e6, null, e5);
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

    private void setEditTextWatcher(final EditText currentEditText, final EditText nextEditText, final  EditText prevEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() == 1) {
                    if(nextEditText != null) {
                        nextEditText.requestFocus();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        currentEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if(currentEditText.getText().length() == 0) {
                        if (prevEditText != null) {
                            prevEditText.getText().clear();
                            prevEditText.requestFocus();
                        }
                    }else{
                        currentEditText.getText().clear();
                    }
                    return true;
                }
                return false;
            }
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(VerificationPage.this, Home.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }, 3000);
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
        databaseReference.child(userId).child("name").setValue(null);
        databaseReference.child(userId).child("dob").setValue(null);
        databaseReference.child(userId).child("wallet").setValue(false);
        databaseReference.child(userId).child("walletBalance").setValue(0.0);

        List<Map.Entry<String, String>> savedPlacesList = new ArrayList<>();
        Map.Entry<String, String> entry;
        entry = new AbstractMap.SimpleEntry<>("Add Home", null);
        savedPlacesList.add(entry);
        entry = new AbstractMap.SimpleEntry<>("Add Work", null);
        savedPlacesList.add(entry);

        Map<String, Map.Entry<String, String>> dataMap = new HashMap<>();
        for (int i = 0; i < savedPlacesList.size(); i++) {
            dataMap.put("place_" + i, savedPlacesList.get(i));
        }

        databaseReference.child(userId).child("saved_places").setValue(dataMap);
        databaseHelper.storeSavedPlacesList(savedPlacesList, userId);

//        Map.Entry<String, String> entry1 = new AbstractMap.SimpleEntry<>("Add Home", null);
//        Map.Entry<String, String> entry2 = new AbstractMap.SimpleEntry<>("Add Work", null);
//        databaseReference.child(userId).child("saved_places").child("place_0").setValue(entry1);
//        databaseReference.child(userId).child("saved_places").child("place_1").setValue(entry2);


        writeDataIntoPreference(phone, null, null, false, (float) 0.0, null);
    }

    private void fetchUserData(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            private String imageUrl;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    String phone = snapshot.child("phone").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String dob = snapshot.child("dob").getValue(String.class);
                    boolean hasWallet = Boolean.TRUE.equals(snapshot.child("wallet").getValue(Boolean.class));
                    float walletBalance = snapshot.child("walletBalance").getValue(Integer.class);

                    StorageReference profilePicRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + userId + ".jpg");
                    profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                    });

                    fetchSavedPlaces(userId);
                    fetchBookingHistory(userId);
                    writeDataIntoPreference(phone,name, dob, hasWallet, walletBalance, imageUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchBookingHistory(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Bookings");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            /** @noinspection DataFlowIssue*/
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    databaseHelper.clearBookingHistory();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String key = dataSnapshot.getKey();
                        String bid = snapshot.child(key).child("bid").getValue(String.class);
                        String source = snapshot.child(key).child("source").getValue(String.class);
                        String destination = snapshot.child(key).child("destination").getValue(String.class);
                        int fullPrice = snapshot.child(key).child("fullPrice").getValue(Integer.class);
                        int halfPrice = snapshot.child(key).child("halfPrice").getValue(Integer.class);
                        int fullCounter = snapshot.child(key).child("fullCounter").getValue(Integer.class);
                        int halfCounter = snapshot.child(key).child("halfCounter").getValue(Integer.class);
                        int totalFullPrice = snapshot.child(key).child("totalFullPrice").getValue(Integer.class);
                        int totalHalfPrice = snapshot.child(key).child("totalHalfPrice").getValue(Integer.class);
                        int totalPrice = snapshot.child(key).child("totalPrice").getValue(Integer.class);
                        String date = snapshot.child(key).child("date").getValue(String.class);
                        String time = snapshot.child(key).child("time").getValue(String.class);
                        String status = snapshot.child(key).child("status").getValue(String.class);
                        String travel = snapshot.child(key).child("travel").getValue(String.class);

                        TicketData ticketData = new TicketData(key, bid, source, destination, fullPrice, halfPrice, fullCounter, halfCounter, totalFullPrice, totalHalfPrice, totalPrice, date, time, status, travel);
                        databaseHelper.addBookingDetails(ticketData, userId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchSavedPlaces(String userId) {
        DatabaseReference savedPlacesReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("saved_places");
        savedPlacesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Map.Entry<String, String>> savedPlacesList = new ArrayList<>();

                if (snapshot.exists()) {
                    for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                        String placeKey = placeSnapshot.getKey();
                        String title = snapshot.child(placeKey).child("key").getValue(String.class);
                        String address = snapshot.child(placeKey).child("value").getValue(String.class);
                        savedPlacesList.add(new AbstractMap.SimpleEntry<>(title, address));
                    }
                }
                databaseHelper.storeSavedPlacesList(savedPlacesList, userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VerificationPage.this, "Something went wrong..!!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void writeDataIntoPreference(String phone, String name, String dob, boolean wallet, float walletBalance, String imageUrl) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone",phone);
        editor.putString("name",name);
        editor.putString("dob",dob);
        editor.putBoolean("wallet",wallet);
        editor.putFloat("walletBalance", walletBalance);
        editor.putString("imagePath",imageUrl);
        editor.apply();
    }
}
