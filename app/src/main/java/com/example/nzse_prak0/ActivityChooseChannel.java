package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nzse_prak0.helpers.DownloadTask;
import com.example.nzse_prak0.helpers.TileAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivityChooseChannel extends AppCompatActivity implements OnChannelScanCompleted {
    ChannelManager channelManager = new ChannelManager();

    private TileAdapter tileAdapter;

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
        tileAdapter = new TileAdapter(channels);
        recyclerView.setAdapter(tileAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        createListeners(tileAdapter);
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

        final Button btnScanChannels = findViewById(R.id.btnScanChannels);
        btnScanChannels.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DownloadTask d = new DownloadTask(ActivityChooseChannel.this, channelManager);
                d.execute();
                Toast t = Toast.makeText(getApplicationContext(), "Bitte warten...", Toast.LENGTH_SHORT);
                t.show();
                btnScanChannels.setEnabled(false);
            }
        });
    }

    @Override
    public void onChannelScanCompleted(Boolean success) {
        if (success) {
            Toast toast = Toast.makeText(getApplicationContext(), "Kanäle gescannt!", Toast.LENGTH_SHORT);
            toast.show();
            tileAdapter.setChannelList(channelManager.getChannels());
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Fehler beim Channel-Scan!", Toast.LENGTH_SHORT);
            toast.show();
        }
        findViewById(R.id.btnScanChannels).setEnabled(true);
    }
}
