package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity implements HistoryListAdapter.OnItemClickListener, OnMapReadyCallback {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    ArrayList<String> stopNames = new ArrayList<>();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    CardView bookTicket, searchBus, selectStop;
    LinearLayout selectBusNumber, profileView;
    TextView sourceEntry, destinationEntry, nav_header_view;
    RecyclerView historyView;
    ImageView imgWallet, swapBtn;
    LocationHelper locationHelper = new LocationHelper(this);
    GoogleMap gMap;
    String userId;
    SharedPreferences cookies;

    ImageView swipeView;
    private static final String TAG = "SwipeDebug";
    private GestureDetector gestureDetector;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_home);

//        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap);
//        supportMapFragment.getMapAsync(this);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bookTicket = findViewById(R.id.bookTicket);
        searchBus = findViewById(R.id.searchBus);
        sourceEntry = findViewById(R.id.sourceEntry);
        destinationEntry = findViewById(R.id.destinationEntry);
        historyView = findViewById(R.id.historyView);
        selectBusNumber = findViewById(R.id.selectBusNumber);
        selectStop = findViewById(R.id.selectStop);
        imgWallet = findViewById(R.id.imgWallet);
        swapBtn = findViewById(R.id.swapBtn);
        mAuth = FirebaseAuth.getInstance();
        profileView = navigationView.getHeaderView(0).findViewById(R.id.profile_View);
        nav_header_view = navigationView.getHeaderView(0).findViewById(R.id.nav_header_view);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cookies = getSharedPreferences("Cookies", Context.MODE_PRIVATE);

        appThemeSetup();
        navigationListener();
        setupSlider();
        langSetup();
        stopNames = databaseHelper.getStopNames();
        locationHelper.startFetchingLocation();

        swipeView = findViewById(R.id.swipeView);
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        swipeView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        imgWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtil.isWalletActivated(Home.this)) {
                    startActivity(new Intent(Home.this, WalletPage.class));
                }
                else{
                    FragmentWalletActivation activateWallet = new FragmentWalletActivation();
                    activateWallet.show(getSupportFragmentManager(), activateWallet.getTag());
                }
            }
        });

        sourceEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, InputActivity.class);
                intent.putExtra("entry","Source");
                intent.putStringArrayListExtra("stopNames",stopNames);
                startActivityForResult(intent, 1);
            }
        });

        destinationEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, InputActivity.class);
                intent.putExtra("entry","Destination");
                intent.putStringArrayListExtra("stopNames",stopNames);
                startActivityForResult(intent, 2);
            }
        });

        swapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapInputEntry();
            }
        });

        bookTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyUtil.isWalletActivated(Home.this)) {
                    checkAndProceed(Ticket_Summary.class);
                }
                else{
                    FragmentWalletActivation activateWallet = new FragmentWalletActivation();
                    activateWallet.show(getSupportFragmentManager(), activateWallet.getTag());
                }
            }
        });

        searchBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndProceed(EligibleBusList.class);
            }
        });

        selectBusNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, SearchBusEntry.class));
            }
        });

        selectStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, allBusStops.class);
                intent.putExtra("Entry", "");
                intent.putExtra("position", -1);
                startActivity(intent);
            }
        });

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, ProfilePage.class));
            }
        });
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 20;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    Intent intent = new Intent(Home.this, MetroHome.class);
                    SharedPreferences.Editor editor = cookies.edit();
                    editor.putString("homePage", "MetroHome").apply();
                    recreate();
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    return true;
                }
            }
            return false;
        }
    }

    private void langSetup() {
        String lang = cookies.getString("appLang", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void setupSlider() {
        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.slider1, ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.slider2,ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.slider3,ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.slider4,ScaleTypes.CENTER_CROP));
        ImageSlider imageSlider = findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);
    }

    private void appThemeSetup() {
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_theme);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch themeSwitchBtn = (Switch) menuItem.getActionView().findViewById(R.id.themeSwitchBtn);

        SharedPreferences sharedPreferences = getSharedPreferences("Cookies",Context.MODE_PRIVATE);
        boolean isDarkModeEnabled = sharedPreferences.getBoolean("theme",false);
        if(isDarkModeEnabled){
            themeSwitchBtn.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        themeSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("theme", isChecked).apply();
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
    }
    public void swapInputEntry(){
        String sourceText = sourceEntry.getText().toString();
        String destinationText = destinationEntry.getText().toString();

        destinationEntry.setText(sourceText);
        sourceEntry.setText(destinationText);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            sourceEntry.setText(data.getStringExtra("userInput"));
        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            destinationEntry.setText(data.getStringExtra("userInput"));
        }
    }

    private void checkAndProceed(Class<?> nextActivity) {
        String source = sourceEntry.getText().toString();
        String destination = destinationEntry.getText().toString();

        if (valid_stop_check(source, destination)) {
            Intent intent = new Intent(Home.this, nextActivity);
            ArrayList<String> eligibleBuses = databaseHelper.getEligibleBuses(source, destination);
            if (eligibleBuses.size() != 0) {
                databaseHelper.insertSearchHistory(userId, source, destination, "Bus");
                intent.putExtra("source", source);
                intent.putExtra("destination", destination);
                intent.putStringArrayListExtra("eligibleBuses", eligibleBuses);
                startActivity(intent);
            } else {
                FragmentNoRoute fragmentNoRoute = new FragmentNoRoute();
                fragmentNoRoute.show(getSupportFragmentManager(), fragmentNoRoute.getTag());
            }
        }
    }
    private Boolean valid_stop_check(String source, String destination) {
        if (source.isEmpty() && destination.isEmpty()){
            Toast.makeText(this, "Enter Source & Destination", Toast.LENGTH_SHORT).show();
            return false;
        } else if (source.isEmpty()) {
            Toast.makeText(this, "Enter Source", Toast.LENGTH_SHORT).show();
            return false;
        } else if (destination.isEmpty()) {
            Toast.makeText(this, "Enter Destination", Toast.LENGTH_SHORT).show();
            return false;
        } else if (source.equals(destination)){
            Toast.makeText(this, "Source & Destination cannot be same!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return stopNames.contains(source) && stopNames.contains(destination);
    }
    public void toggleDrawer(View view) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
    private void navigationListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.option_logout) {
                    mAuth.signOut();
                    startActivity(new Intent(Home.this, LoginPage.class));
                    finish();
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(Home.this, SettingsActivity.class));
                } else if (id == R.id.nav_language) {
                    Intent intent = new Intent(Home.this, LanguageSelection.class);
                    intent.putExtra("entry", "");
                    startActivity(intent);
                } else if (id == R.id.nav_help) {
                    startActivity(new Intent(Home.this, Help.class));
                } else if (id == R.id.nav_fav) {
                    Intent intent = new Intent(Home.this, SavedPlaces.class);
                    intent.putExtra("entry", "");
                    startActivity(intent);
                } else if (id == R.id.nav_myBookings){
                    startActivity(new Intent(Home.this, BookingHistory.class));
                } else if (id == R.id.nav_about){
                    startActivity(new Intent(Home.this, About.class));
                }

                drawerLayout.closeDrawers();
                return false;
            }
        });
    }

    public void openScanner(View view){
        Intent intent = new Intent(Home.this, ScannerActivity.class);
        startActivity(intent);
    }

    public void openBookingHistory(View v){
        startActivity(new Intent(Home.this, BookingHistory.class));
    }


    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences userData = getSharedPreferences("UserData",Context.MODE_PRIVATE);
        String userName = userData.getString("name","");
        if(userName.equals("")){
            nav_header_view.setText("User Name");
        }
        else{
            nav_header_view.setText(userName);
        }

        List<String> lastSearchHistory = databaseHelper.getLastSearchHistory(userId, "Bus");
        HistoryListAdapter historyListAdapter = new HistoryListAdapter( this, lastSearchHistory, sourceEntry, destinationEntry);
        historyView.setAdapter(historyListAdapter);
    }

    @Override
    public void onItemClick(String item) {
        checkAndProceed(Ticket_Summary.class);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        LatLng location = new LatLng(18.4512, 73.93412);
        googleMap.addMarker(new MarkerOptions().position(location).title("Hadapsar"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
    }

    private boolean doubleBackToExitPressedOnce = false;
    private static final int TIME_INTERVAL = 2000;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to Exit!", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, TIME_INTERVAL);
    }
}