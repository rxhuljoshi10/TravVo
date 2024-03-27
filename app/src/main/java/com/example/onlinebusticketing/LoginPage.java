package com.example.onlinebusticketing;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class LoginPage extends AppCompatActivity {
    TextView btnText;
    EditText phone_editText;
    ScrollView scrollView;
    ProgressBar pBar;
    private FirebaseAuth mAuth;
    CardView generate_otp_button;
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_login_page);

        mAuth = FirebaseAuth.getInstance();
        pBar = findViewById(R.id.pBar);
        generate_otp_button = findViewById(R.id.generate_otp);
        btnText = findViewById(R.id.btnText);
        phone_editText = findViewById(R.id.phone);
        scrollView = findViewById(R.id.scrollView);

        if (alreadyUser()){
            startActivity(new Intent(LoginPage.this, Home.class));
            finish();
        }

        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

//        text_eng.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setLocal(LoginPage.this, "en");
//                finish();
//                startActivity(getIntent());
//            }
//        });
//        text_hi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setLocal(LoginPage.this, "hi");
//                finish();
//                startActivity(getIntent());
//            }
//        });

        generate_otp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phone_editText.getText().toString();
                if(TextUtils.isEmpty(phone) || phone.length() != 10){
                    Toast.makeText(LoginPage.this, "Enter Valid Phone number..!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    toggleProgressBar();
                    otpSend(phone);
                }
            }
        });

        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.slider1_loginpage,ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.slider2_loginpage,ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.slider3_loginpage,ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.slider4_loginpage,ScaleTypes.CENTER_CROP));
        ImageSlider imageSlider = findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);

        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isKeyboardOpen()) {
                    scrollUpToMyWantedPosition();
                }
            }
        };

        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    private void scrollUpToMyWantedPosition() {
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int scrollY = calculateScrollY(scrollView, generate_otp_button);
                scrollView.smoothScrollTo(0, scrollY);
            }
        }, 200);
    }

    private int calculateScrollY(ScrollView scrollView, View targetView) {
        int scrollViewHeight = scrollView.getHeight();
        int targetViewY = targetView.getTop();

        return Math.max(0, targetViewY - scrollViewHeight / 2);
    }

    private boolean isKeyboardOpen() {
        int heightDiff = scrollView.getRootView().getHeight() - scrollView.getHeight();
        return heightDiff > 100; // adjust this threshold as needed
    }


    private void toggleProgressBar() {
        int visibility = pBar.getVisibility();
        if(visibility == View.VISIBLE){
//            btnText.setText("Continue");
            pBar.setVisibility(View.GONE);
        }
        else{
//            btnText.setText("");
            pBar.setVisibility(View.VISIBLE);
        }
    }

    private boolean alreadyUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null;
    }

    private void otpSend(String phone) {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(LoginPage.this, "Verification Completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                toggleProgressBar();
                Toast.makeText(LoginPage.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token){
                Intent intent = new Intent(LoginPage.this, VerificationPage.class);
                intent.putExtra("phone_number",phone);
                intent.putExtra("verificationID",verificationId);
                startActivity(intent);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }



    @Override
    protected void onStart() {
        super.onStart();
        pBar.setVisibility(View.INVISIBLE);
    }
}