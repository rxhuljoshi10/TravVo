package com.example.onlinebusticketing;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class Temperory extends AppCompatActivity {

    private Button btnGenerateQR;
    private ImageView imageViewQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperory);

        btnGenerateQR = findViewById(R.id.btnGenerateQR);
        imageViewQR = findViewById(R.id.imageViewQR);

        btnGenerateQR.setOnClickListener(v -> {
            String data = "Dynamic QR Data"; // Your dynamic data here
            generateQRCode(data);
        });
    }

    private void generateQRCode(String data) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap bitmap = toBitmap(bitMatrix);
            imageViewQR.setImageBitmap(bitmap);
            imageViewQR.setVisibility(android.view.View.VISIBLE);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private Bitmap toBitmap(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
            }
        }
        return bmp;
    }

//    private void generateQRCode(String data) {
//        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
//        try {
//            BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
//
//            // Find the boundaries of the QR code
//            int left = bitMatrix.getWidth();
//            int top = bitMatrix.getHeight();
//            int right = 0;
//            int bottom = 0;
//
//            for (int y = 0; y < bitMatrix.getHeight(); y++) {
//                for (int x = 0; x < bitMatrix.getWidth(); x++) {
//                    if (bitMatrix.get(x, y)) {
//                        if (x < left) left = x;
//                        if (x > right) right = x;
//                        if (y < top) top = y;
//                        if (y > bottom) bottom = y;
//                    }
//                }
//            }
//
//            // Add a border size
//            int borderSize = 10; // Adjust as needed
//
//            // Expand the boundaries to include the border
//            left = Math.max(0, left - borderSize);
//            top = Math.max(0, top - borderSize);
//            right = Math.min(bitMatrix.getWidth() - 1, right + borderSize);
//            bottom = Math.min(bitMatrix.getHeight() - 1, bottom + borderSize);
//
//            // Calculate the width and height of the QR code with the border
//            int width = right - left + 1;
//            int height = bottom - top + 1;
//
//            // Create a bitmap with the QR code and border
//            int[] pixels = new int[width * height];
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    boolean isBorder = x < borderSize || x >= width - borderSize || y < borderSize || y >= height - borderSize;
//                    pixels[y * width + x] = isBorder ? android.graphics.Color.WHITE : bitMatrix.get(left + x, top + y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE;
//                }
//            }
//
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//
//            imageViewQR.setImageBitmap(bitmap);
//            imageViewQR.setVisibility(android.view.View.VISIBLE);
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }
//    }



}
