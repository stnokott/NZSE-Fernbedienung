package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nzse_prak0.helpers.TileAdapter;

public class ActivityChooseFavorite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosefavorite);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFavorite);
        recyclerView.setHasFixedSize(true); // bessere Performance, wenn Layout-Größe sich nicht ändert

        GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        String[] tileNames = {"ZDF", "BR", "Super RTL"};
        TileAdapter tileAdapter = new TileAdapter(tileNames);
        recyclerView.setAdapter(tileAdapter);

        createListeners(tileAdapter);
    }

    private void createListeners(TileAdapter tileAdapter) {
        // Listener für Tiles
        tileAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TileAdapter.TileViewHolder viewHolder = (TileAdapter.TileViewHolder) v.getTag();

                String channelName = viewHolder.getChannelTile().getTitle();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("program", channelName);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
