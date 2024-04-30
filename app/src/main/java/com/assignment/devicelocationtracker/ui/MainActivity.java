package com.assignment.devicelocationtracker.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.assignment.devicelocationtracker.R;
import com.assignment.devicelocationtracker.databinding.ActivityMainBinding;
import com.assignment.devicelocationtracker.listener.OnDownloadCompleteListener;
import com.assignment.devicelocationtracker.listener.OnSaveCompleteListener;
import com.assignment.devicelocationtracker.model.LocationData;
import com.assignment.devicelocationtracker.remote.DownloadLocationDataTask;
import com.assignment.devicelocationtracker.remote.SaveLocationDataTask;
import com.assignment.devicelocationtracker.service.LocationUpdateService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final List<LocationData> locationList = new ArrayList<>();
    private ActivityMainBinding binding;
    private LocationUpdateService.LocationUpdateServiceBinder serviceBinder;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBinder = (LocationUpdateService.LocationUpdateServiceBinder) service;
            observeLocationUpdates();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnStart.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, LocationUpdateService.class);
            startService(serviceIntent);
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        });

        binding.btnStop.setOnClickListener(v -> {
            if (serviceBinder != null) {
                serviceBinder.getService().stopLocationUpdates();
                unbindService(serviceConnection);
                serviceBinder = null;
            }
        });

        binding.btnDownload.setOnClickListener(v -> {
            if (locationList.isEmpty()) {
                Toast.makeText(this, getString(R.string.no_location_data_available), Toast.LENGTH_SHORT).show();
            } else {
                saveLocationDataToFile(locationList);
            }
        });
    }

    private void observeLocationUpdates() {
        if (serviceBinder != null) {
            serviceBinder.getService().locationLiveData.observe(this, new Observer<Location>() {
                @Override
                public void onChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    long timestamp = System.currentTimeMillis();
                    LocationData locationData = new LocationData(latitude, longitude, timestamp);
                    locationList.add(locationData);
                    Log.d("MainActivity", "New location: " + location.getLatitude() + ", " + location.getLongitude());

                    binding.tvLatValue.setText(String.valueOf(latitude));
                    binding.tvLonValue.setText(String.valueOf(longitude));
                }
            });
        }
    }

    private void saveLocationDataToFile(List<LocationData> locationList) {
        SaveLocationDataTask saveTask = new SaveLocationDataTask(this, new OnSaveCompleteListener() {
            @Override
            public void onSaveComplete(boolean success) {
                if (success) {
                    // Save operation completed successfully, start the download process
                    downloadLocationData();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to save location data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        saveTask.execute(locationList);
    }

    private void downloadLocationData() {
        DownloadLocationDataTask downloadTask = new DownloadLocationDataTask(this, new OnDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(File file) {
                if (file != null) {
                    Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.example.fileprovider", file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "text/csv");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "File does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
        downloadTask.execute();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service to avoid memory leaks
        if (serviceBinder != null) {
            unbindService(serviceConnection);
            serviceBinder = null;
        }
        locationList.clear();
    }
}