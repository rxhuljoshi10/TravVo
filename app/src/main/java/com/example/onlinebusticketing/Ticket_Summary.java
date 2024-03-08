package com.example.onlinebusticketing;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class Ticket_Summary extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    ArrayList<String> eligibleBuses = new ArrayList<>();
    TextView counterTextView,counterTextView2, sourceView, destinationView, priceView, priceView1,priceView2, walletBalanceView;
    Button payBtn;
    LinearLayout walletView;
    ImageView viewDraggable;
    View coverview;
    private Button btnSwipe;
    private float initialX;
    ProgressBar progressBar;
    private int counter = 1;
    int price = 5;
    int totalPrice = price;
    float walletBalance;
    int originalBtnWidth;

    final static int animationDuration = 400;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_summary);

        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        String destination = intent.getStringExtra("destination");
        eligibleBuses = intent.getStringArrayListExtra("eligibleBuses");
        String bus = eligibleBuses.get(0);
        price = getPrice(bus, source, destination);

        sourceView = findViewById(R.id.sourceView);
        destinationView = findViewById(R.id.destinationView);
        priceView = findViewById(R.id.priceView);
        priceView.setText(" ₹"+price);
        priceView1 = findViewById(R.id.priceView1);
        priceView2 = findViewById(R.id.priceView2);
        counterTextView = findViewById(R.id.counterTextView);
        counterTextView2 = findViewById(R.id.counterTextView2);
        viewDraggable = findViewById(R.id.viewDraggable);
        btnSwipe = findViewById(R.id.btnSwipe);
        progressBar = findViewById(R.id.progressBar);
        walletView = findViewById(R.id.walletView);
        walletBalanceView = findViewById(R.id.walletBalanceView);
        sourceView.setText(source);
        destinationView.setText(destination);
        coverview = findViewById(R.id.coverView);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_key_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.busList);
        BusListAdapter adapter = new BusListAdapter(eligibleBuses, source, destination);
        recyclerView.setAdapter(adapter);

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
                    btnSwipe.setText(getString(R.string.pay) + " ₹"+totalPrice);
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
                startActivity(new Intent(Ticket_Summary.this, TicketView.class));
            }
        },1000);
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

    @Override
    protected void onStart() {
        super.onStart();
        walletBalance = MyUtil.getWalletBalance(this);
        walletBalanceView.setText("Balance : ₹"+String.valueOf(walletBalance));
    }
}