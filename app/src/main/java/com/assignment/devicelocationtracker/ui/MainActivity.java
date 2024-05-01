package com.assignment.devicelocationtracker.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.assignment.devicelocationtracker.utils.ColorUtils;
import com.assignment.devicelocationtracker.utils.Constants;
import com.assignment.devicelocationtracker.utils.FileUtils;
import com.assignment.devicelocationtracker.utils.PermissionUtils;
import com.assignment.devicelocationtracker.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final List<LocationData> locationList = new ArrayList<>();
    private ActivityMainBinding binding;
    private LocationUpdateService.LocationUpdateServiceBinder serviceBinder;
    private ServiceConnection serviceConnection;
    private Observer<Location> locationObserver;

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
        binding.btnStart.setOnClickListener(v -> requestLocationUpdates());
        binding.btnStop.setOnClickListener(v -> stopLocationService());
        binding.btnDownload.setOnClickListener(v -> downloadLocationData());

        serviceConnection = new ServiceConnection() {
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

        locationObserver = location -> {
            updateUIWithLocation(location);
            saveLocationData(location);
        };
    }

    private void requestLocationUpdates() {
        if (PermissionUtils.hasLocationPermission(this)) {
            binding.progressCircular.setVisibility(View.VISIBLE);
            Intent serviceIntent = new Intent(this, LocationUpdateService.class);
            startService(serviceIntent);
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION);
        }
    }

    private void stopLocationService() {
        if (serviceBinder != null) {
            serviceBinder.getService().stopLocationUpdates();
            unbindService(serviceConnection);
            serviceBinder = null;
            ToastUtils.showShortToast(this, getString(R.string.stopped_location_service));
        }
    }

    private void observeLocationUpdates() {
        if (serviceBinder != null) {
            serviceBinder.getService().locationLiveData.observe(this, locationObserver);
        }
    }

    private void updateUIWithLocation(Location location) {
        binding.progressCircular.setVisibility(View.GONE);
        binding.tvLatValue.setText(String.valueOf(location.getLatitude()));
        binding.tvLonValue.setText(String.valueOf(location.getLongitude()));
        ColorUtils.setRandomTextColor(binding.tvLatValue);
        ColorUtils.setRandomTextColor(binding.tvLonValue);
    }

    private void saveLocationData(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        long timestamp = System.currentTimeMillis();
        LocationData locationData = new LocationData(latitude, longitude, timestamp);
        locationList.add(locationData);
    }

    private void downloadLocationData() {
        if (locationList.isEmpty()) {
            ToastUtils.showShortToast(this, getString(R.string.no_location_data_available));
        } else {
            SaveLocationDataTask saveTask = new SaveLocationDataTask(this, success -> {
                if (success) {
                    downloadTask();
                } else {
                    ToastUtils.showShortToast(MainActivity.this, getString(R.string.failed_to_save_location_data));
                }
            });
            saveTask.execute(locationList);
        }
    }

    private void downloadTask() {
        DownloadLocationDataTask downloadTask = new DownloadLocationDataTask(this, file -> FileUtils.viewCsvFile(MainActivity.this, file));
        downloadTask.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION) {
            if (PermissionUtils.hasLocationPermission(this)) {
                requestLocationUpdates();
            } else {
                ToastUtils.showShortToast(this, getString(R.string.location_permission_denied));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBinder != null) {
            unbindService(serviceConnection);
            serviceBinder = null;
        }
        locationList.clear();
    }
}
