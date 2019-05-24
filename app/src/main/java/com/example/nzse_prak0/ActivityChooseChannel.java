package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nzse_prak0.helpers.TileAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivityChooseChannel extends AppCompatActivity {
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
    }
}
