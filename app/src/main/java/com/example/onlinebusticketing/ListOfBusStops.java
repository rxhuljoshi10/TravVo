package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class ListOfBusStops extends AppCompatActivity implements OnMapReadyCallback, BusStops_Adapter.OnItemClickListener{
    TextView toolbarTitle;
    RecyclerView bus_list_view;
    ArrayList<String> stops_list = new ArrayList<>();
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    List<Location> inRouteBusStopsLocation = new ArrayList<>();
    ArrayList<String> inRouteBusStopsName = new ArrayList<>();
    List<MarkerOptions> markersList = new ArrayList<>();
    List<LatLng> latLngList = new ArrayList<>();
    int sourceNum, destinNum;
    private LatLng start;
    private LatLng finish;
    private GoogleMap gMap;

    String busNumber;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stops_list);

        bus_list_view = findViewById(R.id.bus_list_view);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        Intent intent = getIntent();
        busNumber = intent.getStringExtra("busNumber");
        String source = intent.getStringExtra("source");
        String destination = intent.getStringExtra("destination");
        String filterBusNumber = busNumber.substring(0, busNumber.indexOf('-'));

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap);
        supportMapFragment.getMapAsync(this);

        toolbarTitle.setText(filterBusNumber);

        stops_list = databaseHelper.getStops(busNumber);
        sourceNum = databaseHelper.getSequenceNumber(source, busNumber);
        destinNum = databaseHelper.getSequenceNumber(destination, busNumber);

        int val = 0;

        int firstSeqNum = databaseHelper.getFirstSequenceNumber(busNumber);
        if(firstSeqNum == 1){
            val = 1;
        }
//        BusStops_Adapter adapter = new BusStops_Adapter(this, stops_list, sourceNum-val, destinNum-val);
//        bus_list_view.setAdapter(adapter);

        Cursor cursor = databaseHelper.getInRouteStops(String.valueOf(sourceNum), String.valueOf(destinNum), busNumber);
        if(cursor != null && cursor.moveToFirst()){
            do{
                Location location = new Location("");
                location.setLatitude(cursor.getDouble(cursor.getColumnIndex("lat")));
                location.setLongitude(cursor.getDouble(cursor.getColumnIndex("long")));
                inRouteBusStopsLocation.add(location);
                inRouteBusStopsName.add(cursor.getString(cursor.getColumnIndex("stop_name")));

            }while (cursor.moveToNext());
        }

        BusStops_Adapter adapter = new BusStops_Adapter(this, stops_list, sourceNum-val, destinNum-val);
        bus_list_view.setAdapter(adapter);

        for (Location location : inRouteBusStopsLocation) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            latLngList.add(latLng);
        }

        start = latLngList.get(0);
        finish = latLngList.get(latLngList.size()-1);
    }

    public void previous(View v){
        getOnBackPressedDispatcher().onBackPressed();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        MarkerOptions startMarkerOptions = new MarkerOptions()
                .position(start)
                .icon(bitmapDescriptorFromVector(ListOfBusStops.this, R.drawable.ic_source));
        gMap.addMarker(startMarkerOptions);
        gMap.addMarker(new MarkerOptions().position(finish));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(start);
        builder.include(finish);
        LatLngBounds bounds = builder.build();
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        drawRoute(start, finish);
    }

    private void drawRoute(LatLng origin, LatLng destination) {
        int i = 0;
        for(LatLng point : latLngList){
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(point)
                    .icon(bitmapDescriptorFromVector(ListOfBusStops.this, R.drawable.icon_bus_stop))
                    .anchor(0.5f, 0.5f)
                    .title(inRouteBusStopsName.get(i));
            gMap.addMarker(markerOptions);
            markersList.add(markerOptions);
            i++;
        }

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngList);
        polylineOptions.color(Color.BLUE)
                .width(20);
        gMap.addPolyline(polylineOptions);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void focusOnLocation(int position) {
        try {

            if (gMap != null) {
                float zoomLevel = 15.0f;
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(position), zoomLevel));
                MarkerOptions markerOptions = markersList.get(position);
                Marker marker = gMap.addMarker(markerOptions);
                marker.showInfoWindow();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(String selectedItem){
        focusOnLocation(inRouteBusStopsName.indexOf(selectedItem));
    }
}