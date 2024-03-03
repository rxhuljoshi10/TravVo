package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "PMPML_Data";
    private static final int DATABASE_VERSION = 1;
//    private static final String TABLE_NAME = "bus_stops";
//    private static final String CREATE_TABLE_1 = "CREATE TABLE "+ TABLE_NAME +"(direction Text, stop_names TEXT, stop_names_mr TEXT);";

    private static final String TABLE_NAME_2 = "SEARCH_HISTORY";
//    private static final String CREATE_TABLE_2 = "CREATE TABLE "+ TABLE_NAME_2 +"(search_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
//            "SOURCE TEXT, DESTINATION TEXT);";
private static final String CREATE_TABLE_2 = "CREATE TABLE "+ TABLE_NAME_2 +"(search_ID INTEGER PRIMARY KEY AUTOINCREMENT, userId varchar(20)," +
        "SOURCE TEXT, DESTINATION TEXT);";

    private static final String TABLE_NAME_3 = "Bus_Routes";
    private static final String CREATE_TABLE_3 = "CREATE TABLE "+ TABLE_NAME_3 + "(route_name varchar(6), stop_seq INTEGER, stop_name TEXT, stop_name_mr TEXT, lat TEXT, long TEXT, stage INTEGER);";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_TABLE_1);
        db.execSQL(CREATE_TABLE_2);
        db.execSQL(CREATE_TABLE_3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_2);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_3);
        onCreate(db);
    }

//    public void insertIntoStops(List<List<String>> data) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        db.beginTransaction();
//        try {
//            for (List<String> rowData : data) {
//                values.put("direction", rowData.get(0));
//                values.put("stop_names", rowData.get(1));
//                values.put("stop_names_mr", rowData.get(2));
//                db.insert(TABLE_NAME, null, values);
//            }
//            db.setTransactionSuccessful(); // Mark the transaction as successful
//        } finally {
//            db.endTransaction(); // End the transaction
//            db.close(); // Close the database connection
//        }
//    }

    public void insertIntoBusRoutes(List<List<String>> data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        db.beginTransaction();
        try {
            for (List<String> rowData : data) {
                values.put("route_name", rowData.get(1));
                values.put("stop_seq", rowData.get(3));
                values.put("stop_name", rowData.get(4));
                values.put("stop_name_mr", rowData.get(5));
                values.put("lat", rowData.get(6));
                values.put("long", rowData.get(7));
                values.put("stage", rowData.get(8));

                db.insert(TABLE_NAME_3, null, values);
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<String> getBusStops(){
        List<String> temp = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT stop_name FROM "+TABLE_NAME_3;
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String stop = cursor.getString(cursor.getColumnIndex("stop_name"));
                temp.add(stop);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return  temp;
    }

//    public void clearAllData() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_NAME, null, null);
//        db.close();
//    }

    public void
    insertSearchHistory(String userID, String source, String destination){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userID", userID);
        values.put("SOURCE", source);
        values.put("DESTINATION", destination);
        db.insert(TABLE_NAME_2,null, values);
        db.close();
    }

    public List<String> getLastSearchHistory(String userID) {
        List<String> searchHistory = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT SOURCE, DESTINATION FROM " + TABLE_NAME_2 +
                " WHERE userId = ? ORDER BY search_ID DESC LIMIT 5";

        Cursor cursor = null;
        cursor = db.rawQuery(query, new String[]{userID});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String source = cursor.getString(cursor.getColumnIndex("SOURCE"));
                @SuppressLint("Range") String destination = cursor.getString(cursor.getColumnIndex("DESTINATION"));
                searchHistory.add(source + " - " + destination);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return searchHistory;
    }

    public ArrayList<String> getEligibleBuses(String source, String destination){
        ArrayList<String> eligibleBuses = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT a.route_name " +
                "FROM Bus_Routes a " +
                "JOIN Bus_Routes b ON a.route_name = b.route_name " +
                "WHERE a.stop_name = ? " +
                "AND b.stop_name = ? " +
                "AND a.stop_seq < b.stop_seq";


        Cursor cursor = db.rawQuery(query, new String[]{source, destination});
        if(cursor!=null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String temp = cursor.getString(cursor.getColumnIndex("route_name"));
                eligibleBuses.add(temp);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return eligibleBuses;

    }

    public Cursor getStages(String bus, String source, String destination){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT stage FROM "+ TABLE_NAME_3+
                " WHERE route_name = ?"+
                " AND (stop_name = ? OR stop_name = ?);";

        Cursor cursor = db.rawQuery(query,new String[]{bus, source, destination});

//        cursor.close();
        return cursor;
    }

    public ArrayList<String> getStops(String busNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> stops = new ArrayList<>();
        String query = "SELECT stop_name FROM "+ TABLE_NAME_3+
                " WHERE route_name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{busNumber});
        if(cursor!=null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String temp = cursor.getString(cursor.getColumnIndex("stop_name"));
                stops.add(temp);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return stops;
    }

    @SuppressLint("Range")
    public int getSequenceNumber(String stopName, String busNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        int stop_seq = -1, first_stop_seq = 0;

        String query1 = "SELECT stop_seq FROM "+TABLE_NAME_3+
                " WHERE route_name = ? LIMIT 1";
        Cursor cursor1 = db.rawQuery(query1, new String[]{busNumber});
        if (cursor1.moveToFirst()) {
            first_stop_seq = cursor1.getInt(cursor1.getColumnIndex("stop_seq"));
        }

        String query = "SELECT stop_seq FROM "+ TABLE_NAME_3+
                " WHERE route_name = ? AND stop_name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{busNumber, stopName});
        if (cursor.moveToFirst()) {
            stop_seq = cursor.getInt(cursor.getColumnIndex("stop_seq"));
        }

        cursor.close();
        cursor1.close();

        if(first_stop_seq == 1){
            stop_seq -= 1;
        }

        return stop_seq;
    }

    @SuppressLint("Range")
    public int getTotalStops(String busNumber, String source, String destination) {
        SQLiteDatabase db = this.getReadableDatabase();
        int totalStops = 0;
        String query = "";
        Cursor cursor;
        if(source.isEmpty() || destination.isEmpty()){
            query = "SELECT COUNT(*) AS stop_count FROM "+TABLE_NAME_3+
                    " WHERE route_name = ?";
            cursor = db.rawQuery(query, new String[]{busNumber});
        }
        else {
            query = "SELECT COUNT(*) AS stop_count" +
                    " FROM " + TABLE_NAME_3 +
                    " WHERE route_name = ?" +
                    " AND stop_seq BETWEEN" +
                    " (SELECT stop_seq FROM " + TABLE_NAME_3 + " WHERE stop_name = ? AND route_name = ?) AND" +
                    " (SELECT stop_seq FROM " + TABLE_NAME_3 + " WHERE stop_name = ? AND route_name = ?);";
            cursor = db.rawQuery(query, new String[]{busNumber, source, busNumber, destination, busNumber});
        }
        if (cursor.moveToFirst()) {
            totalStops = cursor.getInt(cursor.getColumnIndex("stop_count"));
        }
        cursor.close();
        return totalStops;
    }

    public ArrayList<String> getAllBusNumbers() {
        ArrayList<String> busNumbers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT route_name FROM "+TABLE_NAME_3;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor!=null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String temp = cursor.getString(cursor.getColumnIndex("route_name"));
                busNumbers.add(temp);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return busNumbers;
    }

    @SuppressLint("Range")
    public String getSource(String busNumber, int val) {
        SQLiteDatabase db = this.getReadableDatabase();
        String temp="", query="";
        if(val == 1){
            query = "SELECT stop_name FROM "+ TABLE_NAME_3 +
                    " WHERE route_name = ? LIMIT 1";
        }
        else{
            query = "SELECT stop_name FROM "+ TABLE_NAME_3 +
                    " WHERE route_name = ? ORDER BY stop_seq DESC LIMIT 1";
        }
        Cursor cursor = db.rawQuery(query, new String[]{busNumber});
        if (cursor.moveToFirst()) {
            temp = cursor.getString(cursor.getColumnIndex("stop_name"));
        }
        cursor.close();
        return temp;
    }

    @SuppressLint("Range")
    public ArrayList<Location> getStopsLocationList(){
        ArrayList<Location> stopsLocationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select lat, long FROM "+TABLE_NAME_3+ " GROUP BY stop_name";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null && cursor.moveToFirst()){
            do{
                Location location = new Location("");
                location.setLatitude(cursor.getDouble(cursor.getColumnIndex("lat")));
                location.setLongitude(cursor.getDouble(cursor.getColumnIndex("long")));
                stopsLocationList.add(location);

            }while (cursor.moveToNext());
        }
        cursor.close();
        return stopsLocationList;
    }

    @SuppressLint("Range")
    public ArrayList<String> getStopNames(){
        ArrayList<String> stop_names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select stop_name FROM "+TABLE_NAME_3+ " GROUP BY stop_name";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null && cursor.moveToFirst()){
            do{
                stop_names.add(cursor.getString(cursor.getColumnIndex("stop_name")));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return stop_names;
    }

    public ArrayList<String> getStopBuses(String stopName) {
        ArrayList<String> buses = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT route_name FROM "+TABLE_NAME_3+
                " WHERE stop_name = ?";


        Cursor cursor = db.rawQuery(query, new String[]{stopName});
        if(cursor!=null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String temp = cursor.getString(cursor.getColumnIndex("route_name"));
                buses.add(temp);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return buses;
    }
}

