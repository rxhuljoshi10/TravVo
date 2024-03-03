package com.example.onlinebusticketing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class LocationHelper {
    private final Activity activity;

    public LocationHelper(Activity activity) {
        this.activity = activity;
    }


    public void startFetchingLocation() {
        checkLocationPermission();
        checkLocationSettings();
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        }
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest());

        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity, 1001);
                    } catch (IntentSender.SendIntentException sendEx) {
                        sendEx.printStackTrace();
                    }
                }
            }
        });
    }

    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20000)
                .setFastestInterval(1000);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = createLocationRequest();
        LocationServices.getFusedLocationProviderClient(activity)
            .requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
//                            String address = getAddress(location);
//                            Toast.makeText(activity, address, Toast.LENGTH_SHORT).show();
                            updateLocationInSharedPreferences(location);
                        }
                    }
                }
            }, Looper.getMainLooper());
    }

    private void updateLocationInSharedPreferences(Location location) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lat", String.valueOf(location.getLatitude()));
        editor.putString("long", String.valueOf(location.getLongitude()));
        editor.apply();
    }

    public Location getCurrentLocation() {
        checkLocationSettings();
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        try {
            double latitude = Double.parseDouble(sharedPreferences.getString("lat", "0.0"));
            double longitude = Double.parseDouble(sharedPreferences.getString("long", "0.0"));
            if (latitude != 0.0 && longitude != 0.0) {
                Location location = new Location("SavedLocation");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                return location;
            }
        }
        catch (Exception e){
            Toast.makeText(activity, "Something went wrong, try again later!", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public String getAddress(@NonNull Location location) {
        String address = "";
        try {
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  address;
    }
}

