package com.example.onlinebusticketing;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {
    public static List<List<String>> readCsvData(Context context, String... fileNames) {
        List<List<String>> csvData = new ArrayList<>();

        try {
            for (String fileName : fileNames) {
                // Open each file from the assets directory using the provided method
                try (InputStream inputStream = openFileFromAssets(context, fileName);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] row = line.split(",");
                        List<String> rowData = new ArrayList<>();
                        for (String index : row) {
                            rowData.add(index);
                        }
                        csvData.add(rowData);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return csvData;
    }

    private static InputStream openFileFromAssets(Context context, String fileName) throws IOException {
        AssetManager assetManager = context.getAssets();
        return assetManager.open(fileName);
    }
}
