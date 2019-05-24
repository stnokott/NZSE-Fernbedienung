package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nzse_prak0.helpers.TileAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActivityChooseChannel extends AppCompatActivity {

    protected HttpRequest http = new HttpRequest("172.16.201.122:80", 5000, true);

    ChannelManager channelManager = new ChannelManager();
    ArrayList<Channel> listChannel;

    private int lastPosition = 0;
    private int maxPosition = 0;

    // Channels
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosechannel);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewChannel);
        recyclerView.setHasFixedSize(true); // bessere Performance, wenn Layout-Größe sich nicht ändert

        GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        Channel c1 = new Channel("8a", "Phoenix", "ARD");
        Channel c2 = new Channel("8b", "Bayerisches FS", "ARD");
        Channel c3 = new Channel("8c", "SWR Fernsehen RP", "ARD");
        List<Channel> channels = new ArrayList<>();
        channels.add(c1);
        channels.add(c2);
        channels.add(c3);
        TileAdapter tileAdapter = new TileAdapter(channels);
        recyclerView.setAdapter(tileAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        createListeners(tileAdapter);
    }


    private class DownloadTask extends AsyncTask< Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params){
            // resolving request and do not interrupt UI

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
            Context context;
            CharSequence text;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choosechannel, menu);
        return true;
    }

    private void createListeners(TileAdapter tileAdapter) {
        // Listener für Tiles
        tileAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TileAdapter.TileViewHolder viewHolder = (TileAdapter.TileViewHolder) v.getTag();

                String channelName = viewHolder.getChannelTile().getChannelInstance().getProgram();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("program", channelName);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        Button btnScanChannels = findViewById(R.id.btnScanChannels);
        btnScanChannels.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new DownloadTask().execute();
            }
        });
    }
}
