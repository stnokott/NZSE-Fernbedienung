package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nzse_prak0.helpers.Channel;
import com.example.nzse_prak0.helpers.ChannelManager;
import com.example.nzse_prak0.helpers.DownloadTask;
import com.example.nzse_prak0.helpers.OnDownloadTaskCompleted;

import org.json.JSONObject;

public class ActivitySwitchedOn extends AppCompatActivity implements OnDownloadTaskCompleted {
    public static final ChannelManager channelManager = new ChannelManager();
    private Channel curPlayingChannel = null;

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
                DownloadTask d = new DownloadTask("standby=1", 9, getApplicationContext(), ActivitySwitchedOn.this);
                d.execute();
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

        ImageButton btnPlayingFavorite = findViewById(R.id.btnPlayingFavorite);
        btnPlayingFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavButton();
            }
        });
    }

    public void toggleFavButton() {
        ImageButton btnPlayingFavorite = findViewById(R.id.btnPlayingFavorite);

        Boolean isFav = curPlayingChannel.getIsFav();
        updateFavStatus(isFav);

        Animatable animatable = (Animatable) btnPlayingFavorite.getDrawable();
        animatable.start();

        curPlayingChannel.setIsFav(!isFav);
    }

    public void updateFavStatus(Boolean isFav) {
        ImageButton btnPlayingFavorite = findViewById(R.id.btnPlayingFavorite);
        if (isFav) {
            btnPlayingFavorite.setImageResource(R.drawable.star_fill_reverse_white_anim);
        } else {
            btnPlayingFavorite.setImageResource(R.drawable.star_fill_white_anim);
        }
    }

    public void setCurrentPlayingChannel(Channel channel) {
        TextView lblPlaying = findViewById(R.id.lblPlaying);
        lblPlaying.setText(channel.getProgram());
        updateFavStatus(channel.getIsFav());

        curPlayingChannel = channel;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
            requestCode:
            1 = Channel-Auswahl
            3 = PiP-Auswahl
         */
        if ((requestCode == 3 || requestCode == 1) && resultCode == Activity.RESULT_OK) {
            // für Favoriten-Speicherung
            // TODO: Öfter prüfen? Performance-Probleme?
            ActivitySwitchedOn.channelManager.saveToJSON(getApplicationContext());

            // Daten von aktuellem Main-Channel anzeigen

            int channelAdapterPosition = data.getIntExtra(getString(R.string.intentExtra_channelAdapterPosition_key), 0);
            Channel channelInstance = ActivitySwitchedOn.channelManager.getChannelAt(channelAdapterPosition);
            setCurrentPlayingChannel(channelInstance);
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            int channelAdapterPosition = data.getIntExtra(getString(R.string.intentExtra_channelAdapterPosition_key), 0);
            Channel channelInstance = ActivitySwitchedOn.channelManager.getChannelAt(channelAdapterPosition);
            DownloadTask d = new DownloadTask("channelMain=" + channelInstance.getChannel(), 1, getApplicationContext(), null);
            d.execute();
            setCurrentPlayingChannel(channelInstance);
        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            int channelAdapterPosition = data.getIntExtra(getString(R.string.intentExtra_channelAdapterPosition_key), 0);
            Channel channelInstance = ActivitySwitchedOn.channelManager.getChannelAt(channelAdapterPosition);
            Toast t = Toast.makeText(getApplicationContext(), "Kanal " + channelInstance.getProgram() + " für PiP ausgewählt", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    @Override
    public void onDownloadTaskCompleted(int requestCode, Boolean success, JSONObject jsonObj) {
        /*
            requestCode:
            9 = Standby aktivieren
         */
        switch (requestCode) {
            case 9:
                // Power-Button
                if (success)
                    startActivity(new Intent(ActivitySwitchedOn.this, ActivitySwitchedOff.class));
                // Platz für weitere Responses
        }
    }
}
