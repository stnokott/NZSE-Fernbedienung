package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.nzse_prak0.helpers.OnDownloadTaskCompleted;
import com.example.nzse_prak0.helpers.RequestTask;
import com.example.nzse_prak0.helpers.SharedPrefs;
import com.example.nzse_prak0.helpers.ViewHelper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivitySettings extends AppCompatActivity implements OnDownloadTaskCompleted {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        loadSettings();

        createListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    private void showSnack(String text, int duration, @Nullable String actionText, @Nullable View.OnClickListener actionListener) {
        final CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayoutSettings);
        Snackbar snack = Snackbar.make(coordinatorLayout, text, duration);
        if (actionText != null && actionListener != null) {
            snack.setAction(text, actionListener);
        }
        snack.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnSaveSettings) {
            saveSettings();

            Intent returnIntent = new Intent();
            final Switch toggleBackgroundImg = findViewById(R.id.toggleBackgroundImg);
            int showBackgroundImg = toggleBackgroundImg.isChecked() ? 1 : 0;
            returnIntent.putExtra(getString(R.string.intentExtra_showBackgroundImg_key), showBackgroundImg);

            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createListeners() {
        final Button btnTestConnection = findViewById(R.id.btnTestConnection);
        btnTestConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtIP = findViewById(R.id.txtIP);
                String ip = txtIP.getText().toString();

                final Button btnTestConnection = findViewById(R.id.btnTestConnection);
                btnTestConnection.setEnabled(false); // Button deaktivieren
                // Progress-Indicator zeigen
                ProgressBar progressTestConnection = findViewById(R.id.progressTestConnection);
                progressTestConnection.setVisibility(View.VISIBLE);
                txtIP.clearFocus(); // EditText defokussieren
                // Tastatur verstecken
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtIP.getWindowToken(), 0);
                testConnection(ip, getApplicationContext(), ActivitySettings.this);
            }
        });

        final RadioButton radioMainchannel169 = findViewById(R.id.radioMainchannel169);
        radioMainchannel169.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestTask d = new RequestTask("zoomMain=0", getResources().getInteger(R.integer.requestcode_zoom_main), getApplicationContext(), null);
                d.execute();
            }
        });
        final RadioButton radioMainchannel43 = findViewById(R.id.radioMainchannel43);
        radioMainchannel43.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestTask d = new RequestTask("zoomMain=1", getResources().getInteger(R.integer.requestcode_zoom_main), getApplicationContext(), null);
                d.execute();
            }
        });
        final RadioButton radioPip169 = findViewById(R.id.radioPip169);
        radioPip169.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestTask d = new RequestTask("zoomPip=0", getResources().getInteger(R.integer.requestcode_zoom_pip), getApplicationContext(), null);
                d.execute();
            }
        });
        final RadioButton radioPip43 = findViewById(R.id.radioPip43);
        radioPip43.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestTask d = new RequestTask("zoomPip=1", getResources().getInteger(R.integer.requestcode_zoom_pip), getApplicationContext(), null);
                d.execute();
            }
        });
    }

    @Override
    public void onDownloadTaskCompleted(int requestCode, Boolean success, JSONObject json) {
        if (requestCode == getResources().getInteger(R.integer.requestcode_connectiontest)) {
            // Button aktivieren
            final Button btnTestConnection = findViewById(R.id.btnTestConnection);
            btnTestConnection.setEnabled(true);
            // EditText defokussieren
            final ProgressBar progressTestConnection = findViewById(R.id.progressTestConnection);
            progressTestConnection.setVisibility(View.INVISIBLE);

            if (success) {
                ViewHelper.setViewBackgroundTint(btnTestConnection, getColor(R.color.colorValid));
                showSnack(getString(R.string.lblConnectSuccess), Snackbar.LENGTH_SHORT, null, null);
            } else {
                ViewHelper.setViewBackgroundTint(btnTestConnection, getColor(R.color.colorInvalid));
                showSnack(getString(R.string.lblConnectFailure), Snackbar.LENGTH_SHORT, null, null);
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewHelper.clearBackgroundTint(btnTestConnection);
                }
            }, 3000);
        }
    }

    private void saveSettings() {
        List<Pair<String, Object>> settings = new ArrayList<>();

        final RadioButton radioMainchannel169 = findViewById(R.id.radioMainchannel169);
        int selectedIndex = (radioMainchannel169.isChecked() ? 0 : 1);
        settings.add(new Pair<String, Object>(getString(R.string.preferences_ratio_index_main_key), selectedIndex));

        final RadioButton radioPip169 = findViewById(R.id.radioPip169);
        selectedIndex = (radioPip169.isChecked() ? 0 : 1);
        settings.add(new Pair<String, Object>(getString(R.string.preferences_ratio_index_pip_key), selectedIndex));

        final EditText txtIP = findViewById(R.id.txtIP);
        settings.add(new Pair<String, Object>(getString(R.string.preferences_ip_key), txtIP.getText().toString()));

        final Switch toggleBackgroundImg = findViewById(R.id.toggleBackgroundImg);
        int isSelected = toggleBackgroundImg.isChecked() ? 1 : 0;
        settings.add(new Pair<String, Object>(getString(R.string.preferences_background_img_key), isSelected));

        SharedPrefs.setValues(getApplicationContext(), getString(R.string.preferences_file_name), settings);
    }

    private void loadSettings() {
        String filename = getString(R.string.preferences_file_name);

        String ip = SharedPrefs.getString(getApplicationContext(), filename, getString(R.string.preferences_ip_key), getString(R.string.preferences_ip_default));
        EditText txtIP = findViewById(R.id.txtIP);
        txtIP.setText(ip);

        int selectedIndex = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.preferences_ratio_index_main_key), 0);
        if (selectedIndex == 1) {
            final RadioButton radioMainChannel43 = findViewById(R.id.radioMainchannel43);
            radioMainChannel43.setChecked(true);
        } else {
            final RadioButton radioMainChannel169 = findViewById(R.id.radioMainchannel169);
            radioMainChannel169.setChecked(true);
        }

        selectedIndex = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.preferences_ratio_index_pip_key), 0);
        if (selectedIndex == 1) {
            final RadioButton radioPip43 = findViewById(R.id.radioPip43);
            radioPip43.setChecked(true);
        } else {
            final RadioButton radioPip169 = findViewById(R.id.radioPip169);
            radioPip169.setChecked(true);
        }

        int isSelected = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.preferences_background_img_key), 1);
        final Switch toggleBackgroundImg = findViewById(R.id.toggleBackgroundImg);
        toggleBackgroundImg.setChecked(isSelected == 1);
    }

    public static void testConnection(String ip, Context context, OnDownloadTaskCompleted listener) {
        RequestTask d = new RequestTask("debug=1", context.getResources().getInteger(R.integer.requestcode_connectiontest), listener, ip);
        d.execute();
    }

    public static String getIP(Context context) {
        Context c = context.getApplicationContext();
        return SharedPrefs.getString(c, c.getString(R.string.preferences_file_name), c.getString(R.string.preferences_ip_key), c.getString(R.string.preferences_ip_default));
    }

    public static int getZoomMain(Context context) {
        Context c = context.getApplicationContext();
        return SharedPrefs.getInt(c, c.getString(R.string.preferences_file_name), c.getString(R.string.preferences_ratio_index_main_key), 0);
    }

    public static int getZoomPip(Context context) {
        Context c = context.getApplicationContext();
        return SharedPrefs.getInt(c, c.getString(R.string.preferences_file_name), c.getString(R.string.preferences_ratio_index_pip_key), 0);
    }

    public static int getShowBackgroundImg(Context context) {
        Context c = context.getApplicationContext();
        return SharedPrefs.getInt(c, c.getString(R.string.preferences_file_name), c.getString(R.string.preferences_background_img_key), 1);
    }
}
