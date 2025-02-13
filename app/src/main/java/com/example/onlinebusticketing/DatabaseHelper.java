package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "PMPML_Data";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_2 = "SEARCH_HISTORY";
    private static final String CREATE_TABLE_2 = "CREATE TABLE "+ TABLE_NAME_2 +"(search_ID INTEGER PRIMARY KEY AUTOINCREMENT, userId varchar(20)," +
        "SOURCE TEXT, DESTINATION TEXT);";


    private static final String TABLE_NAME_3 = "Bus_Routes";
    private static final String CREATE_TABLE_3 = "CREATE TABLE "+ TABLE_NAME_3 + "(route_name varchar(6), stop_seq INTEGER, stop_name TEXT, stop_name_mr TEXT, lat TEXT, long TEXT, stage INTEGER);";


    private static final String TABLE_NAME_4 = "RECENT_SEARCHES";
    private static final String CREATE_TABLE_4 = "CREATE TABLE "+ TABLE_NAME_4 +"(search_ID INTEGER PRIMARY KEY AUTOINCREMENT, userId varchar(20)," +
            "stop TEXT);";

    private static final String TABLE_BOOKING_HISTORY = "tableBookingHistory";
    private static final String CREATE_TABLE_5 = "CREATE TABLE "+ TABLE_BOOKING_HISTORY +"(tid INTEGER PRIMARY KEY, uid INTEGER, bid INTEGER, bSource varchar(20), bDestination varchar(20), fullPrice INTEGER, halfPrice INTEGER, fullCounter INTEGER, halfCounter INTEGER, totalFullPrice INTEGER, totalHalfPrice INTEGER, totalPrice INTEGER, bDate varchar(15), bTime varchar(10), status varchar(20));";

    private static final String TABLE_SAVED_PLACES = "SavedPlaces";
    private static final String CREATE_TABLE_6 = "CREATE TABLE "+ TABLE_SAVED_PLACES +"(sid INTEGER PRIMARY KEY AUTOINCREMENT, title varchar(30), address varchar(30));";



    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_2);
        db.execSQL(CREATE_TABLE_3);
        db.execSQL(CREATE_TABLE_4);
        db.execSQL(CREATE_TABLE_5);
        db.execSQL(CREATE_TABLE_6);

        db.execSQL("CREATE TABLE metro_routes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Source TEXT," +
                "Destination TEXT," +
                "Distance REAL," +
                "Stops INTEGER," +
                "Fare REAL)");

        db.execSQL("CREATE TABLE metro_stops (" +
                "Station_ID INTEGER PRIMARY KEY," +  // Unique ID for each station
                "Station_Name TEXT," +               // Metro station name
                "Line TEXT," +                       // Metro line
                "Latitude REAL," +                   // Latitude coordinate
                "Longitude REAL)");
    }

    private void insertPredefinedValues(SQLiteDatabase db) {
//        insertIntoSavedPlacesList("Add Home", null);
//        insertIntoSavedPlacesList("Add Work", null);
        String value = null;
        ContentValues v = new ContentValues();
//        v.put("uid","");
        v.put("title", "Add Home");
        v.put("address", value);
        db.insert(TABLE_SAVED_PLACES, null, v);

        ContentValues v1 = new ContentValues();
        v1.put("title", "Add Work");
        v1.put("address", value);
        db.insert(TABLE_SAVED_PLACES, null, v1);
    }

    private void insertIntoSavedPlacesList(String title, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("title", title);
        v.put("address", address);
        db.insert(TABLE_SAVED_PLACES, null, v);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_2);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_3);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_4);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_BOOKING_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SAVED_PLACES);
        db.execSQL("DROP TABLE IF EXISTS metro_stops");
        db.execSQL("DROP TABLE IF EXISTS metro_routes");
        onCreate(db);
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

    public void insertSearchHistory(String userID, String source, String destination){
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


    public void insertSearchedStop(String userID, String stop){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userID", userID);
        values.put("stop", stop);
        db.insert(TABLE_NAME_4,null, values);
        db.close();
    }

    public ArrayList<String> getRecentSearchedStops(String userID) {
        ArrayList<String> searchHistory = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT stop FROM " + TABLE_NAME_4 +
                " WHERE userId = ? ORDER BY search_ID DESC LIMIT 5";

        Cursor cursor = null;
        cursor = db.rawQuery(query, new String[]{userID});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String stop_name = cursor.getString(cursor.getColumnIndex("stop"));
                searchHistory.add(stop_name);
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
        int stop_seq = -1;
        String query = "SELECT stop_seq FROM "+ TABLE_NAME_3+
                " WHERE route_name = ? AND stop_name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{busNumber, stopName});
        if (cursor.moveToFirst()) {
            stop_seq = cursor.getInt(cursor.getColumnIndex("stop_seq"));
        }
        cursor.close();
        return stop_seq;
    }

    @SuppressLint("Range")
    public int getFirstSequenceNumber(String busNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        int first_stop_seq = 0;

        String query1 = "SELECT stop_seq FROM "+TABLE_NAME_3+
                " WHERE route_name = ? LIMIT 1";
        Cursor cursor1 = db.rawQuery(query1, new String[]{busNumber});
        if (cursor1.moveToFirst()) {
            first_stop_seq = cursor1.getInt(cursor1.getColumnIndex("stop_seq"));
        }
        cursor1.close();
        return first_stop_seq;
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

    @SuppressLint("Range")
    public Cursor getInRouteStops(String sourceNum, String destinNum, String busNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select stop_name, lat, long FROM "+TABLE_NAME_3+ " WHERE route_name = ? AND (stop_seq >= ? AND stop_seq <= ?)";
        return db.rawQuery(query, new String[]{busNumber, sourceNum, destinNum});
    }

    public void addBookingDetails(TicketData bookingDetails, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("tid", bookingDetails.tid);
        v.put("uid", userId);
        v.put("bid", bookingDetails.bookingId);
        v.put("bSource", bookingDetails.source);
        v.put("bDestination", bookingDetails.destination);
        v.put("fullPrice", bookingDetails.fullPrice);
        v.put("halfPrice", bookingDetails.halfPrice);
        v.put("fullCounter", bookingDetails.fullCounter);
        v.put("halfCounter", bookingDetails.halfCounter);
        v.put("totalFullPrice", bookingDetails.totalFullPrice);
        v.put("totalHalfPrice", bookingDetails.totalHalfPrice);
        v.put("totalPrice", bookingDetails.totalPrice);
        v.put("bDate", bookingDetails.tDate);
        v.put("bTime", bookingDetails.tTime);
        v.put("status", bookingDetails.status);

        db.insert(TABLE_BOOKING_HISTORY, null, v);
    }

    public void clearBookingHistory(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKING_HISTORY, null, null);
    }
    @SuppressLint("Range")
    public List<TicketData> getAllBookingDetails(String userId) {
        List<TicketData> bookingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKING_HISTORY+ " WHERE uid = ? ORDER BY tid DESC", new String[]{userId});
        if (cursor.moveToFirst()) {
            do {
                String tid = cursor.getString(cursor.getColumnIndex("tid"));
                String bid = cursor.getString(cursor.getColumnIndex("bid"));
                String source = cursor.getString(cursor.getColumnIndex("bSource"));
                String destination = cursor.getString(cursor.getColumnIndex("bDestination"));
                int fullPrice = cursor.getInt(cursor.getColumnIndex("fullPrice"));
                int halfPrice= cursor.getInt(cursor.getColumnIndex("halfPrice"));
                int fullCounter = cursor.getInt(cursor.getColumnIndex("fullCounter"));
                int halfCounter = cursor.getInt(cursor.getColumnIndex("halfCounter"));
                int totalFullPrice = cursor.getInt(cursor.getColumnIndex("totalFullPrice"));
                int totalHalfPrice = cursor.getInt(cursor.getColumnIndex("totalHalfPrice"));
                int totalPrice = cursor.getInt(cursor.getColumnIndex("totalPrice"));
                String tDate = cursor.getString(cursor.getColumnIndex("bDate"));
                String tTime = cursor.getString(cursor.getColumnIndex("bTime"));
                String status = cursor.getString(cursor.getColumnIndex("status"));

                TicketData bookingDetails = new TicketData(tid, bid, source, destination,fullPrice, halfPrice, fullCounter, halfCounter, totalFullPrice, totalHalfPrice, totalPrice, tDate, tTime, status);
                bookingList.add(bookingDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookingList;
    }

    public void updateBookingStatus(String bid, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", newStatus);

        String selection = "bid = ?";
        String[] selectionArgs = {bid};

        db.update(TABLE_BOOKING_HISTORY, values, selection, selectionArgs);
        db.close();
    }

    public void storeSavedPlacesList(List<Map.Entry<String, String>> dataList, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String[] selectionArgs = { userId };
//        db.delete(TABLE_SAVED_PLACES, "uid = ?", selectionArgs);
        db.delete(TABLE_SAVED_PLACES, null, null);
        for (Map.Entry<String, String> entry : dataList) {
//            values.put("uid", userId);
            values.put("title", entry.getKey());
            values.put("address", entry.getValue());
            db.insert(TABLE_SAVED_PLACES, null, values);
        }
        db.close();
    }

    public List<Map.Entry<String, String>> getSavedPlacesList(String userId) {
        List<Map.Entry<String, String>> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
//        String selection = "uid = ?";
//        String[] selectionArgs = { userId };
        Cursor cursor = db.query(
                TABLE_SAVED_PLACES,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String key = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String value = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                dataList.add(new AbstractMap.SimpleEntry<>(key, value));
            }
            cursor.close();
        }
        db.close();

        return dataList;
    }


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

    public void insertIntoMetroRoutes(List<List<String>> data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        db.beginTransaction();
        try {
            for (List<String> rowData : data) {
                values.clear();

                values.put("Source", rowData.get(0));
                values.put("Destination", rowData.get(1));
                values.put("Distance", Double.parseDouble(rowData.get(2)));
                values.put("Stops", Integer.parseInt(rowData.get(3)));
                values.put("Fare", Double.parseDouble(rowData.get(4)));

                db.insert("metro_routes", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void insertIntoMetroStops(List<List<String>> data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        db.beginTransaction();
        try {
            for (List<String> rowData : data) {
                values.clear();  // ✅ Clear old values

                values.put("Station_ID", Integer.parseInt(rowData.get(0)));  // ID
                values.put("Station_Name", rowData.get(1));                  // Name
                values.put("Line", rowData.get(2));                          // Metro Line
                values.put("Latitude", Double.parseDouble(rowData.get(3)));  // Latitude
                values.put("Longitude", Double.parseDouble(rowData.get(4))); // Longitude

                db.insert("metro_stops", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public double getMetroFare(String source, String destination) {
        SQLiteDatabase db = this.getReadableDatabase();
        double fare = -1; // Default value if no route is found

        Cursor cursor = db.rawQuery("SELECT Fare FROM metro_routes WHERE Source = ? AND Destination = ?",
                new String[]{source, destination});

        if (cursor.moveToFirst()) {
            fare = cursor.getDouble(0); // Fetch fare value
        }
        cursor.close();
        db.close();

        return fare;
    }

    public ArrayList<String> getAllMetroStops() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> stopNames = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT Station_Name FROM metro_stops", null);

        if (cursor.moveToFirst()) {
            do {
                stopNames.add(cursor.getString(0)); // Fetching stop name
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return stopNames;
    }

}

