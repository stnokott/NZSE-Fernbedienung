package com.example.nzse_prak0;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nzse_prak0.helpers.ChannelTileManager;

public class Activity_ChooseChannel extends AppCompatActivity {
    private ChannelTileManager tileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosechannel);

        TableLayout tl = findViewById(R.id.tableLayout);
        tileManager = new ChannelTileManager(tl);

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
