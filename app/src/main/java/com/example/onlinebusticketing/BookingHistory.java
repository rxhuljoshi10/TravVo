package com.example.onlinebusticketing;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class BookingHistory extends AppCompatActivity implements BookingHistoryAdapter.OnItemClickListener {
    RecyclerView recentBookingList;
    Toolbar toolbar;
    List<TicketData> bookingDetailsList = new ArrayList<>();
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        recentBookingList = findViewById(R.id.recentBookingsList);
        textView = findViewById(R.id.textView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_key_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        bookingDetailsList = databaseHelper.getAllBookingDetails(userId);
        if(bookingDetailsList.size() == 0){
            textView.setVisibility(View.VISIBLE);
            recentBookingList.setVisibility(View.GONE);
        }
        BookingHistoryAdapter adapter = new BookingHistoryAdapter(this, bookingDetailsList);
        recentBookingList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(TicketData selectedItem) {
        ArrayList<String> eligibleBuses = databaseHelper.getEligibleBuses(selectedItem.source, selectedItem.destination);
        Intent intent = new Intent(BookingHistory.this, TicketView.class);
        intent.putExtra("ticketData", selectedItem);
        intent.putExtra("eligibleBuses", eligibleBuses);
        intent.putExtra("entry","view");
        startActivity(intent);
    }
}