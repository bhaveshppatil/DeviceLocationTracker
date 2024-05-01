package com.assignment.devicelocationtracker.remote;

import android.content.Context;
import android.os.AsyncTask;

import com.assignment.devicelocationtracker.listener.OnSaveCompleteListener;
import com.assignment.devicelocationtracker.model.LocationData;
import com.assignment.devicelocationtracker.utils.FileUtils;

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
        return FileUtils.writeLocationDataToFile(context, locationList, "location_data.csv");
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (listener != null) {
            listener.onSaveComplete(success);
        }
    }
}
