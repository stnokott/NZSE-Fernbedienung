package com.example.nzse_prak0;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ActivitySwitchedOff extends AppCompatActivity {

    protected HttpRequest http = new HttpRequest("172.16.201.100", 5000, true);

    ChannelManager channelManager = new ChannelManager();
    ArrayList<Channel> listChannel;

    private int lastPosition = 0;
    private int maxPosition = 0;

    // Channels
    JSONObject jsonObject;


    private class DownloadTask extends AsyncTask< Void, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(Void... params){
            // resolving request and do not interrupt UI

            // Asynchron Task for new ChannelManager
            ChannelManager result = new ChannelManager();
            try {
                jsonObject= http.sendHttp("scanChannels=");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObj) {
            if( listChannel != null){
                listChannel.clear();
                scanChannels(jsonObject);
                Toast toast = Toast.makeText(getApplicationContext(), "Channels scanned!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast toast = Toast.makeText(getApplicationContext(), "Scanning channels...", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    void scanChannels(JSONObject jsonObj){

        try {
            channelManager.parseChannels(jsonObj);
            maxPosition = channelManager.channelList.size();
            http.sendHttp("channelMain="+ channelManager.channelList.get(lastPosition));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_off);

        final FloatingActionButton btnSwitchOn = findViewById(R.id.btnSwitchOn);
        btnSwitchOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivitySwitchedOff.this, ActivitySwitchedOn.class));
                finish(); // verhindert, dass man aus ActivitySwitchedOn per Back-Button zurück gehen kann
            }
        });
    }

    @Override
    public void onBackPressed() {
        // tu nichts (verhindert Back-Button-Nutzung)
    }
}
