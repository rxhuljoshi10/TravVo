package com.example.onlinebusticketing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class SuccessPayment extends AppCompatActivity {
    ImageView imageView;
    CardView button;
    TextView continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("Cookies", MODE_PRIVATE);
        String homePage = sharedPreferences.getString("homePage", "Home");

        if (homePage.equals("MetroHome")) {
            setTheme(R.style.Theme_MetroUI);
        } else {
            setTheme(R.style.Theme_BusUI);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_payment);

        imageView = findViewById(R.id.successTick);
        button = findViewById(R.id.button);
        continueBtn = findViewById(R.id.continueButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        Glide.with(this)
                .asGif()
                .load(R.raw.success_tick)
                .placeholder(R.drawable.transparent_bg)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource != null) {
                            resource.start();
                        }
                        return false;
                    }
                })
                .into(imageView);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }


    private void goBack() {
        Intent intent = new Intent(this, WalletPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }


}