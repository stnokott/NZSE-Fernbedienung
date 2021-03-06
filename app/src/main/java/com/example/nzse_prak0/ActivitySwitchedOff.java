package com.example.nzse_prak0;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.nzse_prak0.helpers.OnDownloadTaskCompleted;
import com.example.nzse_prak0.helpers.RequestTask;
import com.example.nzse_prak0.helpers.SharedPrefs;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;


public class ActivitySwitchedOff extends AppCompatActivity implements OnDownloadTaskCompleted {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_off);

        createListeners();

        checkStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_on, menu);
        return true;
    }

    private void createListeners() {
        final ImageButton btnSwitchOn = findViewById(R.id.btnSwitchOn);
        btnSwitchOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestTask d = new RequestTask("standby=0", 9, getApplicationContext(), ActivitySwitchedOff.this);
                d.execute();
                ProgressBar progressSwitchedOff = findViewById(R.id.progressSwitchedOff);
                progressSwitchedOff.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkStatus() {
        final ImageButton btnSwitchOn = findViewById(R.id.btnSwitchOn);
        String commonsFileName = getString(R.string.commons_file_name);
        int standbystate = SharedPrefs.getInt(getApplicationContext(), commonsFileName, getString(R.string.commons_standbystate_key), 1);
        int firstLaunch = SharedPrefs.getInt(getApplicationContext(), commonsFileName, getString(R.string.commons_first_launch_key), 1);
        if (standbystate == 0 && firstLaunch != 1) {
            btnSwitchOn.callOnClick();
        }
        if (firstLaunch == 1) {
            SharedPrefs.setValue(getApplicationContext(), commonsFileName, getString(R.string.commons_first_launch_key), 0);
        }

        // wenn IP noch nicht gesetzt
        if (ActivitySettings.getIP(getApplicationContext()).equals(getString(R.string.preferences_ip_default))) {
            showSnack(getString(R.string.lblNoIpSetHint), Snackbar.LENGTH_INDEFINITE, getString(R.string.lblNoIpSetAction), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActivitySwitchedOff.this, ActivitySettings.class));
                }
            });
        }
    }

    private void showSnack(String text, int duration, @Nullable String actionText, @Nullable View.OnClickListener actionListener) {
        final CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayoutMainOff);
        Snackbar snack = Snackbar.make(coordinatorLayout, text, duration);
        if (actionText != null && actionListener != null) {
            snack.setAction(actionText, actionListener);
        }
        snack.show();
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
            showSnack(getString(R.string.lblConnectFailure), Snackbar.LENGTH_SHORT, null, null);
        }
    }

    @Override
    public void onBackPressed() {
        // tu nichts (verhindert Back-Button-Nutzung)
    }
}
