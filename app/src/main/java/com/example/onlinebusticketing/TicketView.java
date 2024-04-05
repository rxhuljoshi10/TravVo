package com.example.onlinebusticketing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class TicketView extends AppCompatActivity {
    TextView ticketId, dateView, timeView, sourceView, destinationView, totalPriceView,
            counterFullView2, fullSinglePrice, totalFullPriceView,
            counterHalfView2, halfSinglePrice, totalHalfPriceView,
            amountPaid, bookingId;
    ArrayList<String> eligibleBuses = new ArrayList<>();
    TicketData ticketData;

    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    String bid, entry, userId;

    private ImageView imageViewQR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_view);


//        ticketId = findViewById(R.id.ticketId);
        dateView = findViewById(R.id.dateView);
        timeView = findViewById(R.id.timeView);
        sourceView = findViewById(R.id.sourceView);
        destinationView = findViewById(R.id.destinationView);
//        fullSinglePrice = findViewById(R.id.fullSinglePrice);
//        totalFullPriceView = findViewById(R.id.totalFullPriceView);
//        counterFullView2 = findViewById(R.id.counterFullView2);
//        counterHalfView2 = findViewById(R.id.counterHalfView2);
//        halfSinglePrice = findViewById(R.id.halfSinglePrice);
//        totalHalfPriceView = findViewById(R.id.totalHalfPriceView);
        totalPriceView = findViewById(R.id.totalPriceView);
        amountPaid = findViewById(R.id.amountPaid);
        bookingId = findViewById(R.id.bookingId);
        imageViewQR = findViewById(R.id.imageViewQR);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setTicketData();
        saveData();
    }

    private void setTicketData() {
        Intent intent = getIntent();
        ticketData = (TicketData) intent.getSerializableExtra("ticketData");
        eligibleBuses = intent.getStringArrayListExtra("eligibleBuses");
        entry = intent.getStringExtra("entry");
        if(ticketData!=null) {
            sourceView.setText(ticketData.source);
            destinationView.setText(ticketData.destination);
//            counterFullView2.setText(String.valueOf(ticketData.fullCounter));
//            fullSinglePrice.setText(String.valueOf(ticketData.fullPrice));
//            totalFullPriceView.setText(String.valueOf("₹"+ticketData.totalFullPrice));
//
//            counterHalfView2.setText(String.valueOf(ticketData.halfCounter));
//            halfSinglePrice.setText(String.valueOf(ticketData.halfPrice));
//            totalHalfPriceView.setText("₹"+ticketData.totalHalfPrice);

            totalPriceView.setText("₹"+ticketData.totalPrice);
            amountPaid.setText("Amount Paid : ₹"+ticketData.totalPrice);
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR) % 100;
            int month = c.get(Calendar.MONTH)+1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            int hour = c.get(Calendar.HOUR_OF_DAY);

            int min = c.get(Calendar.MINUTE);

            dateView.setText(day +"/"+ month +"/"+year);
            timeView.setText(hour +":"+min);

            bid = getRandomNumbers();
            bookingId.setText("Booking ID : "+bid);
            generateQRCode(bid);
        }
    }

    private String getRandomNumbers() {
        Random random = new Random();
        int min = 10000000; // Minimum 8-digit number
        int max = 99999999; // Maximum 8-digit number
        return String.valueOf(random.nextInt(max - min + 1) + min);
    }

    private void saveData(){
//        if(entry.equals("book")) {
//            TicketData bookingDetails = new TicketData(ticketData.source, ticketData.destination, ticketData.totalPrice, dateView.getText().toString(), timeView.getText().toString(), bid);
//            databaseHelper.addBookingDetails(bookingDetails, userId);
//        }
        TicketData bookingDetails = new TicketData(ticketData.source, ticketData.destination, ticketData.totalPrice, dateView.getText().toString(), timeView.getText().toString(), bid);
        databaseHelper.addBookingDetails(bookingDetails, userId);
    }

    public void displayEligibleBuses(View v){
        Intent intent = new Intent(TicketView.this, EligibleBusList.class);
        intent.putExtra("source", ticketData.source);
        intent.putExtra("destination", ticketData.destination);
        intent.putExtra("eligibleBuses", eligibleBuses);
        startActivity(intent);
    }

    public void displayRoute(View v){
        Intent intent = new Intent(TicketView.this, ListOfBusStops.class);
        intent.putExtra("source", ticketData.source);
        intent.putExtra("destination", ticketData.destination);
        intent.putExtra("busNumber", eligibleBuses.get(0));
        startActivity(intent);
    }

    private void generateQRCode(String data) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, 100, 100);
            Bitmap bitmap = toBitmap(bitMatrix);
            imageViewQR.setImageBitmap(bitmap);
            imageViewQR.setVisibility(android.view.View.VISIBLE);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private Bitmap toBitmap(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : getColor(R.color.primaryColor));
            }
        }
        return bmp;
    }

    public void previous(View v){
        getOnBackPressedDispatcher().onBackPressed();
    }
}