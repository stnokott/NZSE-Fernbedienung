package com.example.nzse_prak0.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.nzse_prak0.ChannelManager;
import com.example.nzse_prak0.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DownloadTask extends AsyncTask< Void, Void, JSONObject> {
    private ChannelManager channelManager;
    private Context context;
    private HttpRequest http = new HttpRequest("172.16.201.122:80", 5000, true);

    public DownloadTask(Context context, ChannelManager channelManager) {
        this.context = context;
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
        scanChannels(jsonObj);
        Toast toast = Toast.makeText(context, "Channels scanned!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast toast = Toast.makeText(context, "Scanning channels...", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void scanChannels(JSONObject jsonObj){

        try {
            channelManager.parseChannels(jsonObj);
            http.sendHttp("channelMain="+ channelManager.getChannelAt(0));
            Context context;
            CharSequence text;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}