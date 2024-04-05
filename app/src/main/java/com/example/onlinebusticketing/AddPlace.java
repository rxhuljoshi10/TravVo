package com.example.onlinebusticketing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AddPlace extends AppCompatActivity {
    EditText editText;
    Button saveBtn;
    ImageView imgClear;
    String stop;
    TextView addressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        Intent intent = getIntent();
        stop = intent.getStringExtra("stop");

        editText = findViewById(R.id.editText);
        editText.setFocusable(true);
        saveBtn = findViewById(R.id.saveBtn);
        imgClear = findViewById(R.id.imgClear);
        addressView = findViewById(R.id.addressView);
        addressView.setText(stop);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(!editText.getText().toString().isEmpty()){
                    saveBtn.setEnabled(true);
                    saveBtn.setTextColor(getResources().getColor(R.color.white));
                    imgClear.setVisibility(View.VISIBLE);

                }
                else{
                    saveBtn.setEnabled(false);
                    saveBtn.setTextColor(getResources().getColor(R.color.grey));
                    imgClear.setVisibility(View.GONE);
                }
            }
        });

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

    }

    public void saveAction(View v){
        String title = editText.getText().toString();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("stop", stop);
        resultIntent.putExtra("title", title);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void previous(View v){
        getOnBackPressedDispatcher().onBackPressed();
    }
}