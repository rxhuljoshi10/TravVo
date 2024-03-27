package com.example.onlinebusticketing;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ProfilePage extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String userId;
    String userName, userDob, userPhone;
    TextView userDOBView, userNameView, userPhoneView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        getUserData();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_key_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userNameView = findViewById(R.id.userNameView);
        userPhoneView = findViewById(R.id.userPhoneView);
        userDOBView = findViewById(R.id.userDOBView);

        userNameView.setText(userName);
        userPhoneView.setText(userPhone);
        userDOBView.setText(userDob);

        viewSetup();
    }

    private void viewSetup() {
        if(userName!=null && (!userName.isEmpty())){
            userNameView.setVisibility(View.VISIBLE);
        }
        else {
            userNameView.setVisibility(View.GONE);
        }

        if(userDob!=null && (!userDob.isEmpty())){
            userDOBView.setVisibility(View.VISIBLE);
        }
        else {
            userDOBView.setVisibility(View.GONE);
        }
    }

    private void getUserData() {
        userName = sharedPreferences.getString("name","");
        userPhone = sharedPreferences.getString("phone","");
        userDob = sharedPreferences.getString("dob","");
    }
    public void showNameInputDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        EditText dialogEditText = dialogView.findViewById(R.id.editTextName);
        dialogEditText.setText(userName);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        builder.setView(dialogView)
                .setTitle("Enter Your Name")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        userName = editTextName.getText().toString();
                        userNameView.setText(userName);
                        updateUserData("name",userName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {}
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void showDatePickerDialog(View v){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    userDOBView.setText(selectedDate);
                    updateUserData("dob",selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void updateUserData(String key, String value) {
        viewSetup();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value).apply();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child(userId).child(key).setValue(value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}