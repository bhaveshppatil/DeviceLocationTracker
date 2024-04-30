package com.assignment.devicelocationtracker.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.assignment.devicelocationtracker.listener.OnDownloadCompleteListener;

import java.io.File;

public class DownloadLocationDataTask extends AsyncTask<Void, Void, File> {
    private Context context;
    private OnDownloadCompleteListener listener;

    public DownloadLocationDataTask(Context context, OnDownloadCompleteListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
    }

    @Override
    protected File doInBackground(Void... voids) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "location_data.csv");
        if (file.exists()) {
            return file;
        }
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        if (listener != null) {
            listener.onDownloadComplete(file);
        }
    }
}
