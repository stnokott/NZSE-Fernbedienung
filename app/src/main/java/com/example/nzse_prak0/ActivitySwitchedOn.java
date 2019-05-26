package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nzse_prak0.helpers.ChannelManager;
import com.example.nzse_prak0.helpers.DownloadTask;

public class ActivitySwitchedOn extends AppCompatActivity {
    public static final ChannelManager channelManager = new ChannelManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_on);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_settings_white_36dp);

        channelManager.loadFromJSON(getApplicationContext());

        createListeners();
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
            startActivity(new Intent(ActivitySwitchedOn.this, ActivitySettings.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // tu nichts (verhindert Back-Button-Nutzung)
    }

    private void createListeners() {
        ImageButton btnSwitchOff = findViewById(R.id.btnSwitchOff);
        btnSwitchOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivitySwitchedOn.this, ActivitySwitchedOff.class));
            }
        });

        Button btnChannels = findViewById(R.id.btnChannels);
        btnChannels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectChannelIntent = new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class);
                selectChannelIntent.putExtra("favsOnly", 0);
                startActivityForResult(selectChannelIntent, 1);
            }
        });

        ImageButton btnFavs = findViewById(R.id.btnFavs);
        btnFavs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent selectChannelIntent = new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class);
                selectChannelIntent.putExtra("favsOnly", 1);
                startActivityForResult(selectChannelIntent, 1);
            }
        });

        ImageButton btnPip = findViewById(R.id.btnPip);
        btnPip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class),  3);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
            requestCode:
            1 = Channel-Auswahl
            3 = PiP-Auswahl
         */
        if (requestCode == 3 || requestCode == 1) {
            // TODO: Öfter prüfen? Performance-Probleme?
            ActivitySwitchedOn.channelManager.saveToJSON(getApplicationContext());
        }

        if (requestCode == 3) {
            if(resultCode == Activity.RESULT_OK){
                String channelName = data.getStringExtra("program");
                Toast t = Toast.makeText(getApplicationContext(), "Kanal "+channelName+" für PiP ausgewählt", Toast.LENGTH_SHORT);
                t.show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast t = Toast.makeText(getApplicationContext(), "Kein Channel gewählt!", Toast.LENGTH_SHORT);
                t.show();
            }
        } else if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String channel = data.getStringExtra("channel");
                DownloadTask d = new DownloadTask(null, "channelMain="+channel);
                d.execute();
            }
        }
    }
}
