package com.assignment.devicelocationtracker.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LocationUpdateService extends Service {
    private static final String TAG = "LocationUpdateService";
    private LocationManager locationManager;
    private LocationListener locationListener;

    private MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    public LiveData<Location> locationLiveData = locationMutableLiveData;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocationUpdateServiceBinder();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Create a new thread for location updates
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d(TAG, "New location: " + location.getLatitude() + ", " + location.getLongitude());
                            locationMutableLiveData.postValue(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            Log.d(TAG, "onStatusChanged: " + provider + ", status: " + status);
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            Log.d(TAG, "onProviderEnabled: " + provider);
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            Log.d(TAG, "onProviderDisabled: " + provider);
                        }
                    };
                    // Register the location listener with the GPS provider
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
                    Looper.loop();
                }
            }).start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public void stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
            Log.d(TAG, "Location updates stopped successfully");
        } else {
            Log.e(TAG, "Unable to stop location updates. LocationManager or LocationListener is null.");
        }
    }

    public class LocationUpdateServiceBinder extends Binder {
        public LocationUpdateService getService() {
            return LocationUpdateService.this;
        }
    }
}
