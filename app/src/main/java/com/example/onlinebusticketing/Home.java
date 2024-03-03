package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
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
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    ArrayList<String> stopNames = new ArrayList<>();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth mAuth;
    CardView bookTicket, searchBus, selectStop;
    LinearLayout selectBusNumber;
    TextView sourceEntry, destinationEntry;
    RecyclerView historyView;
    ImageView imgWallet, swapBtn;
    LocationHelper locationHelper = new LocationHelper(this);
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_home);


        mAuth = FirebaseAuth.getInstance();
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

        navigationListener();

        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        stopNames = databaseHelper.getStopNames();
        locationHelper.startFetchingLocation();

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
                checkAndProceed(Ticket_Summary.class);
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
                Intent intent = new Intent(Home.this, InputActivity.class);
                intent.putExtra("entry","Enter Stop Name");
                intent.putStringArrayListExtra("stopNames",stopNames);
                startActivity(intent);
            }
        });


        themeSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("Cookies",Context.MODE_PRIVATE);
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
                databaseHelper.insertSearchHistory(userId, source, destination);
                intent.putExtra("source", source);
                intent.putExtra("destination", destination);
                intent.putStringArrayListExtra("eligibleBuses", eligibleBuses);
                startActivity(intent);
            } else {
                Toast.makeText(Home.this, "No bus found for this route..!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Home.this, "Invalid Bus Stop", Toast.LENGTH_SHORT).show();
        }
    }
    private Boolean valid_stop_check(String source, String destination) {
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
                Toast.makeText(Home.this, "Test", Toast.LENGTH_SHORT).show();
                int id = item.getItemId();

                if (id == R.id.option_logout) {
                    mAuth.signOut();
                    startActivity(new Intent(Home.this, LoginPage.class));
                    finish();
                }

                drawerLayout.closeDrawers();
                return false;
            }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.wallet) {
//            startActivity(new Intent(Home.this, WalletPage.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressLint("RestrictedApi")
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (menu instanceof MenuBuilder) {
//            ((MenuBuilder) menu).setOptionalIconsVisible(true);
//        }
//        getMenuInflater().inflate(R.menu.option_menu, menu);
//        return true;
//    }

    @Override
    protected void onStart() {
        super.onStart();
        List<String> lastSearchHistory = databaseHelper.getLastSearchHistory(userId);
        HistoryListAdapter historyListAdapter = new HistoryListAdapter(Home.this, lastSearchHistory, sourceEntry, destinationEntry);
        historyView.setAdapter(historyListAdapter);
    }

}