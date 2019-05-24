package com.example.nzse_prak0.helpers;

import android.os.AsyncTask;

import com.example.nzse_prak0.ChannelManager;
import com.example.nzse_prak0.HttpRequest;
import com.example.nzse_prak0.OnChannelScanCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DownloadTask extends AsyncTask< Void, Void, JSONObject> {
    private ChannelManager channelManager;
    private HttpRequest http = new HttpRequest("172.16.201.122", 5000, true);

    private OnChannelScanCompleted listener;

    public DownloadTask(OnChannelScanCompleted listener, ChannelManager channelManager) {
        this.listener = listener;
        this.channelManager = channelManager;
    }

    @Override
    protected JSONObject doInBackground(Void... params){
        // resolving request and do not interrupt UI
        try {
            JSONObject jsonObject = http.sendHttp("scanChannels=");
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObj) {
        if (jsonObj == null) {
            listener.onChannelScanCompleted(false);
        } else {
            scanChannels(jsonObj);
            listener.onChannelScanCompleted(true);
        }
    }

    public void scanChannels(JSONObject jsonObj){

        try {
            channelManager.parseChannels(jsonObj);
            http.sendHttp("channelMain="+ channelManager.getChannelAt(0));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}