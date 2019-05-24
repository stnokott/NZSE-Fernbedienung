package com.example.nzse_prak0;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActivitySwitchedOff extends AppCompatActivity {

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
