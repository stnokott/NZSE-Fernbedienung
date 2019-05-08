package com.example.nzse_prak0;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Activity_SwitchedOff extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main_off);

        final LottieAnimationView btnSwitchLottie = findViewById(R.id.btnSwitchLottie);
        btnSwitchLottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSwitchLottie.playAnimation();
            }
        });

        FloatingActionButton btnSwitchOn = findViewById(R.id.btnSwitchOn);
        btnSwitchOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_SwitchedOff.this, Activity_SwitchedOn.class));
                finish(); // verhindert, dass man aus Activity_SwitchedOn per Back-Button zurück gehen kann
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public void onBackPressed() {
        // tu nichts (verhindert Back-Button-Nutzung)
    }
}
