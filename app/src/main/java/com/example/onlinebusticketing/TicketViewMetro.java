package com.example.onlinebusticketing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;

public class TicketViewMetro extends AppCompatActivity {
    TextView ticketId, dateView, timeView, sourceView, destinationView, totalPriceView,
            fullTicketView, halfTicketView, amountPaid, bookingId, titleView;
    ArrayList<String> eligibleBuses = new ArrayList<>();

    TicketData ticketData;

    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    String entry, homePage;
    private ImageView imageViewQR;
    Button confirmBtn;
    LinearLayout cancelTicketView, bookAgainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        SharedPreferences sharedPreferences = getSharedPreferences("Cookies", MODE_PRIVATE);
//        homePage = sharedPreferences.getString("homePage", "Home");
//
//        if (homePage.equals("MetroHome")) {
//            setTheme(R.style.Theme_MetroUI);
//        } else {
//            setTheme(R.style.Theme_BusUI);
//        }
        setTheme(R.style.Theme_MetroUI);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_view_metro);


//        ticketId = findViewById(R.id.ticketId);
        dateView = findViewById(R.id.dateView);
        timeView = findViewById(R.id.timeView);
        sourceView = findViewById(R.id.sourceView);
        destinationView = findViewById(R.id.destinationView);
        fullTicketView = findViewById(R.id.fullTicketView);

        totalPriceView = findViewById(R.id.totalPriceView);
        amountPaid = findViewById(R.id.amountPaid);
        bookingId = findViewById(R.id.bookingId);
        imageViewQR = findViewById(R.id.imageViewQR);
        confirmBtn = findViewById(R.id.confirmBtn);
        cancelTicketView = findViewById(R.id.cancelView);
        titleView = findViewById(R.id.titleView);
        bookAgainView = findViewById(R.id.bookAgainView);

        setTicketData();
    }

    private void setTicketData() {
        Intent intent = getIntent();
        ticketData = (TicketData) intent.getSerializableExtra("ticketData");
        entry = intent.getStringExtra("entry");

        if(ticketData!=null) {
            sourceView.setText(ticketData.source);
            destinationView.setText(ticketData.destination);

            totalPriceView.setText("₹"+ticketData.totalPrice);
            fullTicketView.setText("Passengers : "+ ticketData.fullCounter+" x ₹"+ticketData.fullPrice);
            amountPaid.setText("Amount Paid : ₹"+ticketData.totalPrice);

            dateView.setText(ticketData.tDate);
            timeView.setText(ticketData.tTime);

            bookingId.setText("Booking ID : "+ ticketData.bookingId);

            if(!ticketData.status.equals("Cancelled")) {
                imageViewQR.setVisibility(View.VISIBLE);
//                confirmBtn.setVisibility(View.VISIBLE);
//                bookAgainView.setVisibility(View.GONE);
                cancelTicketView.setVisibility(View.VISIBLE);
                generateQRCode(ticketData.bookingId);
            }
            else {
                titleView.setText("Booking Cancelled");
            }
        }
    }

    public void displayEligibleBuses(View v){
        Intent intent = new Intent(TicketViewMetro.this, EligibleBusList.class);
        intent.putExtra("source", ticketData.source);
        intent.putExtra("destination", ticketData.destination);
        intent.putExtra("eligibleBuses", eligibleBuses);
        startActivity(intent);
    }

    public void displayRoute(View v){
        Intent intent = new Intent(TicketViewMetro.this, ListOfBusStops.class);
        intent.putExtra("source", ticketData.source);
        intent.putExtra("destination", ticketData.destination);
        intent.putExtra("busNumber", eligibleBuses.get(0));
        startActivity(intent);
    }

    private void generateQRCode(String data) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, 150, 150);
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
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : getColor(R.color.grey));
            }
        }
        return bmp;
    }

    public void cancelTicket(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm Cancellation..!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(TicketViewMetro.this, "Ticket Cancelled!", Toast.LENGTH_SHORT).show();
                        databaseHelper.updateBookingStatus(ticketData.bookingId, "Cancelled");
                        FirebaseDatabase db = FirebaseDatabase.getInstance();

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference databaseReference = db.getReference("Users").child(userId).child("Bookings").child(ticketData.tid);
                        databaseReference.child("status").setValue("Cancelled");
                        getOnBackPressedDispatcher().onBackPressed();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .show();
    }
    public void previous(View v){
        getOnBackPressedDispatcher().onBackPressed();
        finish();
    }

    public void bookAgain(View view) {
        Intent intent = new Intent(this, Ticket_Summary.class);
        intent.putExtra("source", ticketData.source);
        intent.putExtra("destination", ticketData.destination);
        intent.putExtra("eligibleBuses", eligibleBuses);
        startActivity(intent);
    }
}