package com.example.nzse_prak0;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nzse_prak0.helpers.OnDownloadTaskCompleted;
import com.example.nzse_prak0.helpers.RequestTask;
import com.example.nzse_prak0.helpers.SharedPrefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;


public class ActivitySwitchedOff extends AppCompatActivity implements OnDownloadTaskCompleted {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_off);

        final FloatingActionButton btnSwitchOn = findViewById(R.id.btnSwitchOn);
        btnSwitchOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestTask d = new RequestTask("standby=0", 9, getApplicationContext(), ActivitySwitchedOff.this);
                d.execute();
                ProgressBar progressSwitchedOff = findViewById(R.id.progressSwitchedOff);
                progressSwitchedOff.setVisibility(View.VISIBLE);
            }
        });

        int standbystate = SharedPrefs.getInt(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_standbystate_key), 1);
        int firstLaunch = SharedPrefs.getInt(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_first_launch_key), 1);
        if (standbystate == 0 && firstLaunch != 1) {
            btnSwitchOn.callOnClick();
        }
        if (firstLaunch == 1) {
            SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_first_launch_key), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_on, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnSettings) {
            startActivity(new Intent(ActivitySwitchedOff.this, ActivitySettings.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDownloadTaskCompleted(int requestCode, Boolean success, JSONObject json) {
        ProgressBar progressSwitchedOff = findViewById(R.id.progressSwitchedOff);
        progressSwitchedOff.setVisibility(View.INVISIBLE);
        if (success) {
            startActivity(new Intent(ActivitySwitchedOff.this, ActivitySwitchedOn.class));
            SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_standbystate_key), 0);
            finish(); // verhindert, dass man aus ActivitySwitchedOn per Back-Button zurück gehen kann
        } else {
            Toast.makeText(getApplicationContext(), "TV aktivieren fehlgeschlagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        // tu nichts (verhindert Back-Button-Nutzung)
    }
}
