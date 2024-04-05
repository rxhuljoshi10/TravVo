package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;



public class ProfilePage extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String userId;
    String userName, userDob, userPhone;
    TextView userDOBView, userNameView, userPhoneView;
    ImageView profilePicView;


    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        getUserData();

        profilePicView = findViewById(R.id.profilePicView);
        userNameView = findViewById(R.id.userNameView);
        userPhoneView = findViewById(R.id.userPhoneView);
        userDOBView = findViewById(R.id.userDOBView);

        userNameView.setText(userName);
        userPhoneView.setText("+91 "+userPhone);
        userDOBView.setText(userDob);

        viewSetup();
    }

    private void viewSetup() {
        if(userName!=null && (!userName.isEmpty())){
            userNameView.setVisibility(View.VISIBLE);
        }
        else {
            userNameView.setVisibility(View.GONE);
        }

        if(userDob!=null && (!userDob.isEmpty())){
            userDOBView.setVisibility(View.VISIBLE);
        }
        else {
            userDOBView.setVisibility(View.GONE);
        }

        String profileImageReference = sharedPreferences.getString("imagePath", null);
        if (profileImageReference != null) {
            Uri profileImageUri = Uri.fromFile(new File(profileImageReference));
            profilePicView.setImageURI(profileImageUri);
        }
        else {
            loadImageFromFirebaseStorage(userId);
        }
    }

    private void getUserData() {
        userName = sharedPreferences.getString("name","");
        userPhone = sharedPreferences.getString("phone","");
        userDob = sharedPreferences.getString("dob","");
    }

    public void showNameInputDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        EditText dialogEditText = dialogView.findViewById(R.id.editTextName);
        dialogEditText.setText(userName);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        builder.setView(dialogView)
                .setTitle("Enter Your Name")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        userName = editTextName.getText().toString();
                        userNameView.setText(userName);
                        updateUserData("name",userName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {}
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void openGallery(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            profilePicView.setImageURI(imageUri);
            saveProfileImage(imageUri, userId);
            uploadImageToFirebaseStorage(imageUri , userId);
        }
    }

    public void previous(View v){
        getOnBackPressedDispatcher().onBackPressed();
    }

    public void showDatePickerDialog(View v){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    userDOBView.setText(selectedDate);
                    updateUserData("dob",selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void updateUserData(String key, String value) {
        viewSetup();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value).apply();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child(userId).child(key).setValue(value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void uploadImageToFirebaseStorage(Uri imageUri, String userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("profile_images/" + userId + ".jpg");

        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
            });
        }).addOnFailureListener(exception -> {
            exception.printStackTrace();
        });
    }

    private void loadImageFromFirebaseStorage(String userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("profile_images/" + userId + ".jpg");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            Picasso.get().load(imageUrl).into(profilePicView);
            saveImageToLocalStorage(imageUrl);
        }).addOnFailureListener(exception -> {
            exception.printStackTrace();
        });
    }

    private void saveProfileImage(Uri imageUri, String userId){
        String imagePath = saveImageToInternalStorage(imageUri, userId);
        if (imagePath != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("imagePath", imagePath).apply();
        }
    }

    private String saveImageToInternalStorage(Uri imageUri, String userId){
        try {
            File internalStorageDir = new File(getFilesDir(), "profile_images");
            if (!internalStorageDir.exists()) {
                internalStorageDir.mkdirs();
            }
            File imageFile = new File(internalStorageDir, userId + ".jpg");

            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void saveImageToLocalStorage(String imageUrl) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    File imagesDirectory = new File(getFilesDir(), "profile_images");
                    if (!imagesDirectory.exists()) {
                        imagesDirectory.mkdirs();
                    }

                    String fileName = userId + ".jpg";
                    File imageFile = new File(imagesDirectory, fileName);

                    InputStream inputStream = new URL(imageUrl).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("imagePath", imageFile.getAbsolutePath()).apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}