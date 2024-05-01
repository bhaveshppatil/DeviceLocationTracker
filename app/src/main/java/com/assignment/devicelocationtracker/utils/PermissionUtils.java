package com.assignment.devicelocationtracker.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class PermissionUtils {

    public static boolean hasLocationPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Permissions are granted at install time on older versions of Android
            return true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}

