package com.example.nzse_prak0;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nzse_prak0.helpers.TileAdapter;

public class ActivityChooseChannel extends AppCompatActivity {
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_choosechannel);
        setContentView(R.layout.activity_choosechannel);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // bessere Performance, wenn Layout-Größe sich nicht ändert

        gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        String[] tileNames = {"Pro 7", "Kabeleins", "RTL", "ARD"};
        TileAdapter tileAdapter = new TileAdapter(tileNames);
        recyclerView.setAdapter(tileAdapter);

        createListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choosechannel, menu);
        return true;
    }

    private void createListeners() {

    }
}
