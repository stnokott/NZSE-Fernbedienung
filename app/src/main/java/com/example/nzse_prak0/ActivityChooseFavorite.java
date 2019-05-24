package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nzse_prak0.helpers.TileAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivityChooseFavorite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosefavorite);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFavorite);
        recyclerView.setHasFixedSize(true); // bessere Performance, wenn Layout-Größe sich nicht ändert

        GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        Channel c1 = new Channel("22a", "ZDF", "ZDFmobil");
        Channel c2 = new Channel("22b", "3sat", "ZDFmobil");
        List<Channel> channels = new ArrayList<>();
        channels.add(c1);
        channels.add(c2);
        TileAdapter tileAdapter = new TileAdapter(channels);
        recyclerView.setAdapter(tileAdapter);

        createListeners(tileAdapter);
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
