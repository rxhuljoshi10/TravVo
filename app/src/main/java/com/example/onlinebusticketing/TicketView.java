package com.example.onlinebusticketing;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TicketView extends AppCompatActivity {
    TextView ticketId, dateView, timeView, sourceView, destinationView, totalPriceView,
            counterFullView2, fullSinglePrice, totalFullPriceView,
            counterHalfView2, halfSinglePrice, totalHalfPriceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_view);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_key_left);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        ticketId = findViewById(R.id.ticketId);
        dateView = findViewById(R.id.dateView);
        timeView = findViewById(R.id.timeView);
        sourceView = findViewById(R.id.sourceView);
        destinationView = findViewById(R.id.destinationView);
        fullSinglePrice = findViewById(R.id.fullSinglePrice);
        totalFullPriceView = findViewById(R.id.totalFullPriceView);
        counterFullView2 = findViewById(R.id.counterFullView2);
        counterHalfView2 = findViewById(R.id.counterHalfView2);
        halfSinglePrice = findViewById(R.id.halfSinglePrice);
        totalHalfPriceView = findViewById(R.id.totalHalfPriceView);
        totalPriceView = findViewById(R.id.totalPriceView);


        getTicketData();
    }

    private void getTicketData() {
        Intent intent = getIntent();
        TicketData ticketData = (TicketData) intent.getSerializableExtra("ticketData");
        if(ticketData!=null) {
            sourceView.setText(ticketData.source);
            destinationView.setText(ticketData.destination);
            counterFullView2.setText(String.valueOf(ticketData.fullCounter));
            fullSinglePrice.setText(String.valueOf(ticketData.fullPrice));
            totalFullPriceView.setText(String.valueOf("₹"+ticketData.totalFullPrice));

            counterHalfView2.setText(String.valueOf(ticketData.halfCounter));
            halfSinglePrice.setText(String.valueOf(ticketData.halfPrice));
            totalHalfPriceView.setText("₹"+ticketData.totalHalfPrice);

            totalPriceView.setText("₹"+ticketData.totalPrice);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}