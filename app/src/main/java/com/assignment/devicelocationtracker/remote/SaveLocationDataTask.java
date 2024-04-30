package com.assignment.devicelocationtracker.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.assignment.devicelocationtracker.listener.OnSaveCompleteListener;
import com.assignment.devicelocationtracker.model.LocationData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SaveLocationDataTask extends AsyncTask<List<LocationData>, Void, Boolean> {
    private final Context context;
    private final OnSaveCompleteListener listener;

    public SaveLocationDataTask(Context context, OnSaveCompleteListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(List<LocationData>... lists) {
        List<LocationData> locationList = lists[0];
        if (locationList == null || locationList.isEmpty()) {
            return false;
        }

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "location_data.csv");
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

    @Override
    protected void onPostExecute(Boolean success) {
        if (listener != null) {
            listener.onSaveComplete(success);
        }
    }
}
