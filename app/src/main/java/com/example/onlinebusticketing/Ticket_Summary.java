package com.example.onlinebusticketing;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class Ticket_Summary extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    ArrayList<String> eligibleBuses = new ArrayList<>();
    TextView sourceView, destinationView, walletBalanceView, totalPriceView,
            counterFullView, counterFullView2, fullPriceView, fullSinglePrice, totalFullPriceView,
            counterHalfView, counterHalfView2, halfPriceView, halfSinglePrice, totalHalfPriceView,
            busListLabel, fullText, fullTextSummary, rupeeSymbol;
    LinearLayout walletView, halfPriceParentView, halfPriceParentView2;
    RecyclerView busList;
    ImageView viewDraggable;
    View coverview;
    private Button btnSwipe;
    private float initialX;
    ProgressBar progressBar;
    int fullCounter = 1, halfCounter = 0, fullPrice = 5, halfPrice = 0, totalHalfPrice=0, totalFullPrice = 5;
    int totalPrice = fullPrice;
    float walletBalance;
    int originalBtnWidth;
    String source, destination, userId, homePage;
    final static int animationDuration = 400;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("Cookies", MODE_PRIVATE);
        homePage = sharedPreferences.getString("homePage", "Home");

        if (homePage.equals("MetroHome")) {
            setTheme(R.style.Theme_MetroUI);
        } else {
            setTheme(R.style.Theme_BusUI);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_summary);


        Intent intent = getIntent();
        source = intent.getStringExtra("source");
        destination = intent.getStringExtra("destination");


        sourceView = findViewById(R.id.sourceView);
        destinationView = findViewById(R.id.destinationView);
        fullPriceView = findViewById(R.id.fullPriceView);
        fullPriceView.setText(String.valueOf(fullPrice));
        fullSinglePrice = findViewById(R.id.fullSinglePrice);
        totalFullPriceView = findViewById(R.id.totalFullPriceView);
        counterFullView = findViewById(R.id.counterFullView);
        counterFullView2 = findViewById(R.id.counterFullView2);
        counterHalfView = findViewById(R.id.counterHalfView);
        counterHalfView2 = findViewById(R.id.counterHalfView2);
        halfPriceView = findViewById(R.id.halfPriceView);
        halfSinglePrice = findViewById(R.id.halfSinglePrice);
        totalHalfPriceView = findViewById(R.id.totalHalfPriceView);
        totalPriceView = findViewById(R.id.totalPriceView);
        viewDraggable = findViewById(R.id.viewDraggable);
        btnSwipe = findViewById(R.id.btnSwipe);
        progressBar = findViewById(R.id.progressBar);
        walletView = findViewById(R.id.walletView);
        walletBalanceView = findViewById(R.id.walletBalanceView);
        sourceView.setText(source);
        destinationView.setText(destination);
        coverview = findViewById(R.id.coverView);

        busListLabel = findViewById(R.id.busListLabel);
        busList = findViewById(R.id.busList);
        halfPriceParentView = findViewById(R.id.halfPriceParentView);
        halfPriceParentView2 = findViewById(R.id.halfPriceParentView2);
        fullText = findViewById(R.id.fullText);
        fullTextSummary = findViewById(R.id.fullTextSummary);
        rupeeSymbol = findViewById(R.id.rupeeSymbol);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_key_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(homePage.equals("MetroHome")){
            fullPrice = databaseHelper.getMetroFare(source, destination);
            busListLabel.setVisibility(View.GONE);
            busList.setVisibility(View.GONE);
            halfPriceParentView.setVisibility(View.GONE);
            halfPriceParentView2.setVisibility(View.GONE);
            fullText.setText("Passengers");
            fullTextSummary.setText("Passengers : ");
            rupeeSymbol.setVisibility(View.GONE);
            fullPriceView.setVisibility(View.GONE);
        }
        else{
            eligibleBuses = intent.getStringArrayListExtra("eligibleBuses");
            String bus = eligibleBuses.get(0);
            fullPrice = getPrice(bus, source, destination);
            RecyclerView recyclerView = findViewById(R.id.busList);
            BusListAdapter adapter = new BusListAdapter(eligibleBuses, source, destination);
            recyclerView.setAdapter(adapter);
        }

        updateCounterTextView();
        implementSwipeButton();

        walletView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Ticket_Summary.this, WalletPage.class));
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void implementSwipeButton() {
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
                            updateCoverView(newX+30);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (view.getX() > (btnSwipe.getWidth() - view.getWidth() - 50)) {
                            originalBtnWidth = btnSwipe.getWidth();
                            btnSwipe.setText("");
                            viewDraggable.setVisibility(View.INVISIBLE);
                            coverview.setVisibility(View.GONE);
                            animateButtonWidth(btnSwipe, 190, false);
                        } else {
                            view.setX(0);
                            updateCoverView(0);
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void updateCoverView(float width) {
        ViewGroup.LayoutParams params = coverview.getLayoutParams();
        params.width = (int) width;
        coverview.setLayoutParams(params);
    }

    private void animateButtonWidth(final Button button, int targetWidth, boolean reverse) {
        ValueAnimator widthAnimator = ValueAnimator.ofInt(button.getWidth(), targetWidth);
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = button.getLayoutParams();
                params.width = animatedValue;
                button.setLayoutParams(params);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(widthAnimator);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(animationDuration);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!reverse) {
                    simulateLoading();
                }
                else{
                    viewDraggable.setVisibility(View.VISIBLE);
                    viewDraggable.setX(0);
                    btnSwipe.setText(getString(R.string.pay) +totalPrice);
                    updateCoverView(0);
                    coverview.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animatorSet.start();
    }

    private void simulateLoading() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                float newWalletBalance = walletBalance - (float)totalPrice;
                if(newWalletBalance < 0){
                    inSufficientBalance();
                }
                else{
                    successPayment(newWalletBalance);
                }
            }
        }, 1000);
    }

    public void toggleTicketSummary(View view) {
        LinearLayout ticketBill = findViewById(R.id.ticketBill);
        TextView textView = findViewById(R.id.viewSummaryView);
        ImageView imageView = findViewById(R.id.indicatorImg);
        if (ticketBill.getVisibility() == View.VISIBLE) {
            textView.setText("View Summary");
            ticketBill.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_key_down);
        } else {
            textView.setText("Show Less");
            ticketBill.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_key_up);
        }
    }

    private void successPayment(float newWalletBalance) {
        MyUtil.updateWalletPrice(this, newWalletBalance);
        tickAnimation();
    }

    private void inSufficientBalance() {
        reverseAnimation();
        Toast.makeText(this, "Insufficient Wallet Balance..!!", Toast.LENGTH_SHORT).show();
    }

    private void reverseAnimation() {
        animateButtonWidth(btnSwipe, originalBtnWidth, true);
    }

    private void tickAnimation() {
        ImageView img = (ImageView) findViewById(R.id.tick_animation);
        img.setVisibility(View.VISIBLE);
        ((Animatable) img.getDrawable()).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextPage();
            }
        },1000);
    }

    private void nextPage() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR) % 100;
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        String min = String.format("%02d" ,c.get(Calendar.MINUTE));
        String sec = String.format("%02d" ,c.get(Calendar.SECOND));


        String date = day +"/"+ month +"/"+year;
        String time = hour +":"+min;
        String bid = getRandomNumbers();

        String tid = date + time + sec;
        tid = tid.replace("/","").replace(":","");

        TicketData ticketData;
        if(homePage.equals("MetroHome")){
            ticketData = new TicketData(tid, bid, source, destination, fullPrice, halfPrice, fullCounter, halfCounter, totalFullPrice, totalHalfPrice, totalPrice,date, time, "Booking Succesful","Metro");
        }
        else{
            ticketData = new TicketData(tid, bid, source, destination, fullPrice, halfPrice, fullCounter, halfCounter, totalFullPrice, totalHalfPrice, totalPrice,date, time, "Booking Succesful","Bus");
        }
        saveData(ticketData);

        Intent intent;
        if(homePage.equals("MetroHome")){
            intent = new Intent(Ticket_Summary.this, TicketViewMetro.class);
        }
        else{
            intent = new Intent(Ticket_Summary.this, TicketView.class);
            intent.putExtra("eligibleBuses", eligibleBuses);
        }

        intent.putExtra("ticketData", ticketData);
        intent.putExtra("entry","book");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void saveData(TicketData t){
        databaseHelper.addBookingDetails(t, userId);
        saveDataToFirebase(t);
    }

    private void saveDataToFirebase(TicketData t) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = db.getReference("Users").child(userId).child("Bookings").child(t.tid);
        databaseReference.child("bid").setValue(t.bookingId);
        databaseReference.child("source").setValue(t.source);
        databaseReference.child("destination").setValue(t.destination);
        databaseReference.child("fullPrice").setValue(t.fullPrice);
        databaseReference.child("halfPrice").setValue(t.halfPrice);
        databaseReference.child("fullCounter").setValue(t.fullCounter);
        databaseReference.child("halfCounter").setValue(t.halfCounter);
        databaseReference.child("totalFullPrice").setValue(t.totalFullPrice);
        databaseReference.child("totalHalfPrice").setValue(t.totalHalfPrice);
        databaseReference.child("totalPrice").setValue(t.totalPrice);
        databaseReference.child("date").setValue(t.tDate);
        databaseReference.child("time").setValue(t.tTime);
        databaseReference.child("status").setValue(t.status);
        databaseReference.child("travel").setValue(t.travel);
    }

    private String getRandomNumbers() {
        Random random = new Random();
        int min = 10000000; // Minimum 8-digit number
        int max = 99999999; // Maximum 8-digit number
        return String.valueOf(random.nextInt(max - min + 1) + min);
    }

    public int getPrice(String bus, String source, String destination) {
        String busReverse;
        int Price1=0, Price2=0;
        if(bus.endsWith("U")){
            busReverse = bus.substring(0, bus.length() - 1) + "D";
        }
        else{
            busReverse = bus.substring(0, bus.length() - 1) + "U";
        }

        Cursor cursor = databaseHelper.getStages(bus, source, destination);
        Cursor cursor2 = databaseHelper.getStages(busReverse, source, destination);

        if(cursor!=null && cursor.moveToFirst()) {
            @SuppressLint("Range") int stage1 = cursor.getInt(cursor.getColumnIndex("stage"));
            cursor.moveToNext();
            @SuppressLint("Range") int stage2 = cursor.getInt(cursor.getColumnIndex("stage"));

            Price1 = (int) calcPrice(stage1, stage2);
        }
        try {
            if (cursor2 != null && cursor2.moveToFirst()) {
                @SuppressLint("Range") int stage1 = cursor2.getInt(cursor2.getColumnIndex("stage"));
                cursor2.moveToNext();
                @SuppressLint("Range") int stage2 = cursor2.getInt(cursor2.getColumnIndex("stage"));

                Price2 = (int) calcPrice(stage1, stage2);
            }
        }
        catch (Exception e){
            Price2 = Price1 + 5;
        }

        return Math.max(Price1, Price2);
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
        if(fullCounter < 10){
            fullCounter++;
            updateCounterTextView();
        }
    }

    public void decrementCounter(View view) {
        if (fullCounter > 1) {
            fullCounter--;
            updateCounterTextView();
        }
    }

    public void incrementHalfCounter(View view) {
        if(halfCounter < 10){
            halfCounter++;
            updateCounterTextView();
        }
    }

    public void decrementHalfCounter(View view) {
        if (halfCounter > 0) {
            halfCounter--;
            updateCounterTextView();
        }
    }

    private void updateCounterTextView() {

        totalFullPrice = fullPrice * fullCounter;
        halfPrice = (int) Math.ceil(fullPrice/2.0);
        halfPrice = ((halfPrice + 4) / 5) * 5;
        totalHalfPrice = halfPrice * halfCounter;

        totalPrice = totalFullPrice + totalHalfPrice;
        checkBalanceAndHighlight();

        fullPriceView.setText(String.valueOf(totalFullPrice));
        counterFullView.setText(String.valueOf(fullCounter));
        counterFullView2.setText(String.valueOf(fullCounter));
        fullSinglePrice.setText(String.valueOf(fullPrice));
        totalFullPriceView.setText("₹"+totalFullPrice);

        halfPriceView.setText(String.valueOf(totalHalfPrice));
        counterHalfView.setText(String.valueOf(halfCounter));
        counterHalfView2.setText(String.valueOf(halfCounter));
        halfSinglePrice.setText(String.valueOf(halfPrice));
        totalHalfPriceView.setText("₹"+totalHalfPrice);

        totalPriceView.setText("₹"+totalPrice);

        btnSwipe.setText(getString(R.string.pay) + totalPrice);
    }

    private void checkBalanceAndHighlight() {
        if(totalPrice > walletBalance){
            alertBalance();
        }
        else{
            removeAlertBalance();
        }
    }

    private void removeAlertBalance() {
        totalPriceView.setTextColor(ContextCompat.getColor(this, R.color.iconColor));
    }

    private void alertBalance() {
        totalPriceView.setTextColor(Color.RED);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        getOnBackPressedDispatcher().onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        walletBalance = MyUtil.getWalletBalance(this);
        walletBalanceView.setText("Balance : ₹"+walletBalance);
        checkBalanceAndHighlight();
    }

    public void showTermsCondtions(View view) {
        startActivity(new Intent(Ticket_Summary.this, TermsConditions.class));
    }

}