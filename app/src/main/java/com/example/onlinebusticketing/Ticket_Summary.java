package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Ticket_Summary extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    ArrayList<String> eligibleBuses = new ArrayList<>();

    TextView counterTextView,counterTextView2, sourceView, destinationView, priceView, priceView1,priceView2, walletBalanceView;
    Button payBtn;
    private ImageView viewDraggable;
    private Button btnSwipe;
    private float initialX;
    ProgressBar progressBar;
    private int counter = 1;
    int price = 5;
    int totalPrice = price;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_summary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        sourceView = findViewById(R.id.sourceView);
        destinationView = findViewById(R.id.destinationView);
        priceView = findViewById(R.id.priceView);
        priceView1 = findViewById(R.id.priceView1);
        priceView2 = findViewById(R.id.priceView2);
        counterTextView = findViewById(R.id.counterTextView);
        counterTextView2 = findViewById(R.id.counterTextView2);
//        payBtn = findViewById(R.id.payBtn);
        viewDraggable = findViewById(R.id.viewDraggable);
        btnSwipe = findViewById(R.id.btnSwipe);
        progressBar = findViewById(R.id.progressBar);
        walletBalanceView = findViewById(R.id.walletBalanceView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        String destination = intent.getStringExtra("destination");

        eligibleBuses = intent.getStringArrayListExtra("eligibleBuses");
        RecyclerView recyclerView = findViewById(R.id.busList);
        BusListAdapter adapter = new BusListAdapter(eligibleBuses, source, destination);
        recyclerView.setAdapter(adapter);


        String bus = eligibleBuses.get(0);
        price = getPrice(bus, source, destination);
        priceView.setText(" ₹"+price);

        updateCounterTextView();

        float walletBalance = MyUtil.getWalletBalance(this);
        walletBalanceView.setText("Balance : ₹"+String.valueOf(walletBalance));

        sourceView.setText(source);
        destinationView.setText(destination);

        viewDraggable.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = view.getX() - event.getRawX();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + initialX;
                        if (newX > 0 && newX < (btnSwipe.getWidth() - view.getWidth())) {
                            view.setX(newX);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (view.getX() > (btnSwipe.getWidth() - view.getWidth() - 50)) {
                            simulateLoading();
                        } else {
                            view.setX(0);
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });
//        payBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent1 = new Intent(Ticket_Summary.this, PaymentMethods.class);
//                intent1.putExtra("amount", totalPrice);
//                startActivity(intent1);
//            }
//        });

    }

    public int getPrice(String bus, String source, String destination) {
        Cursor cursor = databaseHelper.getStages(bus, source, destination);

        if(cursor!=null && cursor.moveToFirst()) {
            @SuppressLint("Range") int stage1 = cursor.getInt(cursor.getColumnIndex("stage"));
            cursor.moveToNext();
            @SuppressLint("Range") int stage2 = cursor.getInt(cursor.getColumnIndex("stage"));

            return (int) calcPrice(stage1, stage2);
        }
        return price;
    }
    private double calcPrice(int stage1, int stage2) {
        double n = 0.0;
        double dist =(float) stage2 - stage1;
        if (dist%2 == 0){
            n = 5;
        }
        else{
            n = 2.5;
        }
        return Math.ceil(((dist/2) * 5) + n);
    }

    public void incrementCounter(View view) {
        if(counter <= 10){
            counter++;
            updateCounterTextView();
        }
    }

    public void decrementCounter(View view) {
        if (counter > 1) {
            counter--;
            updateCounterTextView();
        }
    }

    private void updateCounterTextView() {
        totalPrice = price*counter;
        String displayPrice = " ₹"+totalPrice;
        counterTextView.setText(String.valueOf(counter));
        counterTextView2.setText(String.valueOf(counter));
        priceView1.setText(displayPrice);
        priceView2.setText(displayPrice);
        btnSwipe.setText(getString(R.string.pay) + displayPrice);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void simulateLoading() {
        viewDraggable.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Ticket_Summary.this, "Payment Done", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }
}