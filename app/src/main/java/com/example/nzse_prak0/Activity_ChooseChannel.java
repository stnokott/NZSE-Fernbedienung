package com.example.nzse_prak0;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.nzse_prak0.customviews.ChannelTile;
import com.example.nzse_prak0.helpers.ChannelTileManager;

public class Activity_ChooseChannel extends AppCompatActivity {
    private ChannelTileManager tileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosechannel);

        TableLayout tl = findViewById(R.id.tableLayout);
        tileManager = new ChannelTileManager(tl);
        //tileManager.addTile("Schnansch");

        ConstraintLayout testLayout = findViewById(R.id.testLayout);
        ChannelTile newTile = new ChannelTile(testLayout.getContext(), "Schnansch", "0", Color.RED);
        ConstraintSet cset = new ConstraintSet();
        cset.clone(testLayout);
        cset.constrainWidth(newTile.getId(), ConstraintSet.MATCH_CONSTRAINT);
        cset.connect(newTile.getId(), ConstraintSet.END, testLayout.getId(), ConstraintSet.END, 0);
        cset.connect(newTile.getId(), ConstraintSet.BOTTOM, testLayout.getId(), ConstraintSet.BOTTOM, 0);
        cset.applyTo(testLayout);
        testLayout.addView(newTile);

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
