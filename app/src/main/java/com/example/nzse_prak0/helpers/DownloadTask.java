package com.example.nzse_prak0.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nzse_prak0.ActivitySettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DownloadTask extends AsyncTask< Void, Void, JSONObject> {
    private HttpRequest http;
    private String command;
    private int requestCode;

    private OnDownloadTaskCompleted listener;

    public DownloadTask(String command, int requestCode, Context c, OnDownloadTaskCompleted listener) {
        this(command, requestCode, listener, ActivitySettings.getIP(c));
    }

    public DownloadTask(String command, int requestCode, OnDownloadTaskCompleted listener, String ip) {
        this.listener = listener;
        this.command = command;
        this.requestCode = requestCode;
        http = new HttpRequest(ip, 5000, true);
    }

    @Override
    protected JSONObject doInBackground(Void... params){
        // resolving request and do not interrupt UI
        try {
            JSONObject jsonObject = http.sendHttp(this.command);
            return jsonObject;
        } catch (IOException |JSONException e) {
            Log.e("doInBackground", e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObj) {
        if (jsonObj == null) {
            if (listener != null)
                listener.onDownloadTaskCompleted(requestCode, false, null);
        } else {
            if (listener != null)
                listener.onDownloadTaskCompleted(requestCode, true, jsonObj);
        }
    }
}