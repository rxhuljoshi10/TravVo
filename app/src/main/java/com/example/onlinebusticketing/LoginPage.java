package com.example.onlinebusticketing;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class LoginPage extends AppCompatActivity {
    String verificationID;
    TextView text_eng,text_hi,text_mr;
    private FirebaseAuth mAuth;
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        if (alreadyUser()){
            startActivity(new Intent(LoginPage.this, Home.class));
            finish();
        }

        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        mAuth = FirebaseAuth.getInstance();

        text_eng = findViewById(R.id.lang_eng);
        text_eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocal(LoginPage.this, "en");
                finish();
                startActivity(getIntent());
            }
        });

        text_hi = findViewById(R.id.lang_hi);
        text_hi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocal(LoginPage.this, "hi");
                finish();
                startActivity(getIntent());
            }
        });

        CardView generate_otp_button = findViewById(R.id.generate_otp);
        generate_otp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText phone_editText = findViewById(R.id.phone);
                String phone = phone_editText.getText().toString();

                if(TextUtils.isEmpty(phone) || phone.length() != 10){
                    Toast.makeText(LoginPage.this, "Enter Valid Phone number..!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    findViewById(R.id.pBar).setVisibility(View.VISIBLE);
                    otpSend(phone);
                }
            }
        });

        ArrayList<SlideModel> imageList = new ArrayList<>();

        imageList.add(new SlideModel(R.drawable.image1,ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.image2,ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.app_logo, ScaleTypes.CENTER_CROP));

        ImageSlider imageSlider = findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);
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
                findViewById(R.id.pBar).setVisibility(View.INVISIBLE);
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

    public void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.pBar).setVisibility(View.INVISIBLE);
    }
}