package com.assignment.devicelocationtracker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.assignment.devicelocationtracker.model.LocationData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileUtils {

    public static boolean writeLocationDataToFile(Context context, List<LocationData> locationList, String fileName) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        try {
            FileWriter writer = new FileWriter(file);
            for (LocationData locationData : locationList) {
                // Format: latitude,longitude,timestamp
                writer.write(locationData.getLatitude() + "," + locationData.getLongitude() + "," + locationData.getTimestamp() + "\n");
            }
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void viewCsvFile(Context context, File file) {
        if (file != null && file.exists()) {
            Uri uri = FileProvider.getUriForFile(context, "com.example.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "text/csv");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {
            ToastUtils.showShortToast(context, "File does not exist");
        }
    }
}
