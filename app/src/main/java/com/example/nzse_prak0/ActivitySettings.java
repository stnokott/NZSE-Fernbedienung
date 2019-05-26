package com.example.nzse_prak0;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nzse_prak0.helpers.DownloadTask;
import com.example.nzse_prak0.helpers.OnDownloadTaskCompleted;
import com.example.nzse_prak0.helpers.ViewHelper;

import org.json.JSONObject;

public class ActivitySettings extends AppCompatActivity implements OnDownloadTaskCompleted {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        createListeners();

        loadSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createListeners() {
        Button btnTestConnection = findViewById(R.id.btnTestConnection);
        btnTestConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtIP = findViewById(R.id.txtIP);
                String ip = txtIP.getText().toString();
                DownloadTask d = new DownloadTask("", 2, ActivitySettings.this, ip);
                d.execute();

                v.setEnabled(false); // Button deaktivieren
                // Progress-Indicator zeigen
                ProgressBar progressTestConnection = findViewById(R.id.progressTestConnection);
                progressTestConnection.setVisibility(View.VISIBLE);
                txtIP.clearFocus(); // EditText defokussieren
                // Tastatur verstecken
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtIP.getWindowToken(), 0);
            }
        });
    }

    @Override
    public void onDownloadTaskCompleted(int requestCode, Boolean success, JSONObject json) {
        // BUtton aktivieren
        final Button btnTestConnection = findViewById(R.id.btnTestConnection);
        btnTestConnection.setEnabled(true);
        // EditText defokussieren
        final ProgressBar progressTestConnection = findViewById(R.id.progressTestConnection);
        progressTestConnection.setVisibility(View.INVISIBLE);

        if (success) {
            ViewHelper.setViewBackgroundTint(btnTestConnection, getColor(R.color.colorValid));
            Toast.makeText(getApplicationContext(), "Verbindung erfolgreich!", Toast.LENGTH_SHORT).show();
        } else {
            ViewHelper.setViewBackgroundTint(btnTestConnection, getColor(R.color.colorInvalid));
            Toast.makeText(getApplicationContext(), "Verbindung fehlgeschlagen!", Toast.LENGTH_LONG).show();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewHelper.clearBackgroundTint(btnTestConnection);
            }
        }, 3000);
    }

    private void saveSettings() {
        Context c = getApplicationContext();
        SharedPreferences sharedPrefs = c.getSharedPreferences(getString(R.string.preferences_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Spinner selectRatio = findViewById(R.id.selectRatio);
        editor.putInt(getString(R.string.preferences_ratiopos_key), selectRatio.getSelectedItemPosition());

        EditText txtIP = findViewById(R.id.txtIP);
        editor.putString(getString(R.string.preferences_ip_key), txtIP.getText().toString());

        editor.apply();
    }

    private void loadSettings() {
        Context c = getApplicationContext();
        SharedPreferences sharedPrefs = c.getSharedPreferences(getString(R.string.preferences_file_name), MODE_PRIVATE);

        int ratioIndex = sharedPrefs.getInt(getString(R.string.preferences_ratiopos_key), 0);
        Spinner selectRatio = findViewById(R.id.selectRatio);
        selectRatio.setSelection(ratioIndex);

        String ip = sharedPrefs.getString(getString(R.string.preferences_ip_key), getString(R.string.preferences_ip_default));
        EditText txtIP = findViewById(R.id.txtIP);
        txtIP.setText(ip);
    }

    private static String getStringValue(Context c, String key, String def) {
        SharedPreferences sharedPrefs = c.getSharedPreferences(c.getString(R.string.preferences_file_name), MODE_PRIVATE);
        return sharedPrefs.getString(key, def);
    }

    private static int getIntValue(Context c, String key, int def) {
        SharedPreferences sharedPrefs = c.getSharedPreferences(c.getString(R.string.preferences_file_name), MODE_PRIVATE);
        return sharedPrefs.getInt(key, def);
    }

    public static String getRatio(Context context) {
        Context c = context.getApplicationContext();
        int ratioPos = getIntValue(c, c.getString(R.string.preferences_ratiopos_key), 0);
        return c.getResources().getStringArray(R.array.ratios_array)[ratioPos];
    }

    public static String getIP(Context context) {
        Context c = context.getApplicationContext();
        return getStringValue(c, c.getString(R.string.preferences_ip_key), c.getString(R.string.preferences_ip_default));
    }
}
