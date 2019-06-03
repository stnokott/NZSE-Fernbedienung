package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nzse_prak0.helpers.Channel;
import com.example.nzse_prak0.helpers.ChannelManager;
import com.example.nzse_prak0.helpers.DownloadTask;
import com.example.nzse_prak0.helpers.OnDownloadTaskCompleted;
import com.example.nzse_prak0.helpers.SharedPrefs;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ActivitySwitchedOn extends AppCompatActivity implements OnDownloadTaskCompleted {
    public static final ChannelManager channelManager = new ChannelManager();
    private Channel curPlayingChannel = null;

    private static final String CHANNEL_ICON_FILENAMES_DICT_FILE = "filenames.json";
    public static HashMap<String, String> channelIconFilenames = new HashMap<>();
    private boolean play = true;
    private int pausedTime = 0;
    private int volume = 50;
    private int muted = 0;
    private Timer timer =new Timer();
    private int channelPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_on);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_settings_white_36dp);
        final ImageButton btnPipChange = findViewById(R.id.btnPipChange);
        setButtonEnabled(btnPipChange, false);

        channelManager.loadFromJSON(getApplicationContext());
        loadIconFilenamesFromJSON();
        applyLastKnownCommons();


        // Start debug mode and shows a status bar at the bottom
        DownloadTask d = new DownloadTask("debug=1", 0, getApplicationContext(), null);
        d.execute();
        // Timeshift beenden, falls noch läuft
        // TODO: letzt bekannten TImeshift-Status persistent speichern (nicht die Zeit, nur ob pausiert ist)
        DownloadTask d1 = new DownloadTask("timeShiftPlay=0", getResources().getInteger(R.integer.requestcode_timeshift_resume), getApplicationContext(), null);
        d1.execute();

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
        final ImageButton btnSwitchOff = findViewById(R.id.btnSwitchOff);
        btnSwitchOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadTask d = new DownloadTask("standby=1", getResources().getInteger(R.integer.requestcode_standby), getApplicationContext(), ActivitySwitchedOn.this);
                d.execute();
            }
        });

        final Button btnChannels = findViewById(R.id.btnChannels);
        btnChannels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectChannelIntent = new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class);
                selectChannelIntent.putExtra("favsOnly", 0);
                startActivityForResult(selectChannelIntent, getResources().getInteger(R.integer.activitycode_mainchannel));
            }
        });

        final ImageButton btnFavs = findViewById(R.id.btnFavs);
        btnFavs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent selectChannelIntent = new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class);
                selectChannelIntent.putExtra("favsOnly", 1);
                startActivityForResult(selectChannelIntent, getResources().getInteger(R.integer.activitycode_mainchannel));
            }
        });

        final ImageButton btnVolumeUp = findViewById(R.id.btnVolumeUp);
        btnVolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volumeUp();
            }
        });

        final ImageButton btnVolumeDown = findViewById(R.id.btnVolumeDown);
        btnVolumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volumeDown();
            }
        });

        final ImageView btnMute = findViewById(R.id.btnMute);
        btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volumeToggleMute();
            }
        });

        final ImageButton btnPause = findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(play && pausedTime > 0){
                    timeshiftResume();
                } else{
                    timeshiftPause();
                }
            }
        });

        final ImageButton btnPipToggle = findViewById(R.id.btnPipToggle);
        btnPipToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pipstatus = SharedPrefs.getInt(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipstatus_key), 0);
                if (pipstatus == 1) {
                    // wenn Pip schon aktiviert, deaktiviere
                    DownloadTask d = new DownloadTask("showPip=0", getResources().getInteger(R.integer.requestcode_pipdeactivate), getApplicationContext(), ActivitySwitchedOn.this);
                    d.execute();
                } else {
                    // wenn Pip noch nicht aktiviert, aktiviere und wähle Kanal
                    startActivityForResult(new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class), getResources().getInteger(R.integer.activitycode_choosepip));
                }
            }
        });

        final ImageButton btnPipSwap = findViewById(R.id.btnPipChange);
        btnPipSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pipstatus = SharedPrefs.getInt(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipstatus_key), 0);
                if (pipstatus == 1) {
                    startActivityForResult(new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class), getResources().getInteger(R.integer.activitycode_choosepip));
                }
            }
        });

        ImageButton btnPlayingFavorite = findViewById(R.id.btnPlayingFavorite);
        btnPlayingFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curPlayingChannel != null)
                    toggleFavButton();
            }
        });


        ImageButton btnChannelNext = findViewById(R.id.btnChannelNext);
        btnChannelNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                nextChannel();
            }
        });

        ImageButton btnChannelPrevious = findViewById(R.id.btnChannelPrevious);
        btnChannelPrevious.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                previousChannel();
            }
        });
    }

    private void volumeUp() {
        if (volume < 100) {
            // volume-Attribut wird hier noch nicht verändert, erst bei erfolgreichem Request-Callback
            DownloadTask d = new DownloadTask("volume=" + (volume + 1), getResources().getInteger(R.integer.requestcode_volume_up), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }
    }

    private void volumeDown() {
        if (volume > 0) {
            // volume-Attribut wird hier noch nicht verändert, erst bei erfolgreichem Request-Callback
            DownloadTask d = new DownloadTask("volume=" + (volume - 1), getResources().getInteger(R.integer.requestcode_volume_down), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }
    }

    private void volumeToggleMute() {
        // muted-Attribut wird hier noch nicht verändert, erst bei erfolgreichem Request-Callback
        DownloadTask d = new DownloadTask("volume=" + (muted == 0 ? 0 : volume), getResources().getInteger(R.integer.requestcode_mute_change), getApplicationContext(), ActivitySwitchedOn.this);
        d.execute();
    }

    private void nextChannel() {
        int nextIndex;
        if (channelPosition == channelManager.getChannelCount() - 1) {
            // falls Ende erreicht, fange von vorne an
            nextIndex = 0;
        } else {
            nextIndex = channelPosition + 1;
        }
        Channel channel = channelManager.getChannelAt(nextIndex);
        DownloadTask d = new DownloadTask("channelMain=" + channel.getChannel(), getResources().getInteger(R.integer.requestcode_channel_change), getApplicationContext(), null);
        d.execute();

        setCurrentPlayingChannel(nextIndex);
    }

    private void previousChannel() {
        int nextIndex;
        if (channelPosition == 0) {
            // falls Anfang erreicht, fange von hinten an
            nextIndex = channelManager.getChannelCount() - 1;
        } else {
            nextIndex = channelPosition - 1;
        }
        Channel channel = channelManager.getChannelAt(nextIndex);
        DownloadTask d = new DownloadTask("channelMain=" + channel.getChannel(), getResources().getInteger(R.integer.requestcode_channel_change), getApplicationContext(), null);
        d.execute();

        setCurrentPlayingChannel(nextIndex);
    }

    private void timeshiftPause() {
        DownloadTask d = new DownloadTask("timeShiftPause=", getResources().getInteger(R.integer.requestcode_timeshift_pause), getApplicationContext(), ActivitySwitchedOn.this);
        d.execute();
    }

    private void timeshiftResume() {
        DownloadTask d = new DownloadTask("timeShiftPlay=" + pausedTime, getResources().getInteger(R.integer.requestcode_timeshift_resume), getApplicationContext(), ActivitySwitchedOn.this);
        d.execute();
    }

    private void toggleFavButton() {
        ImageButton btnPlayingFavorite = findViewById(R.id.btnPlayingFavorite);

        Boolean isFav = curPlayingChannel.getIsFav();
        updateFavStatus(isFav);

        Animatable animatable = (Animatable) btnPlayingFavorite.getDrawable();
        animatable.start();

        curPlayingChannel.setIsFav(!isFav);
    }

    private void loadIconFilenamesFromJSON() {
        try (JsonReader reader = new JsonReader(new InputStreamReader(getAssets().open(CHANNEL_ICON_FILENAMES_DICT_FILE)))) {
            channelIconFilenames.clear();
            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                String value = reader.nextString();
                channelIconFilenames.put(key, value);
            }
            reader.endObject();
        } catch (IOException e) {
            Log.e("saveToJSON", e.getMessage());
        }
    }

    private void applyLastKnownCommons() {
        // TODO: effizienter machen durch Abfrage als Array
        String filename = getString(R.string.commons_file_name);
        int curChannelIndex = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.commons_channelindex_key), -1);
        if (curChannelIndex != -1 && channelManager.getChannelCount() > curChannelIndex) {
            DownloadTask d = new DownloadTask("channelMain=" + channelManager.getChannelAt(curChannelIndex).getChannel(), getResources().getInteger(R.integer.requestcode_mainchannel), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
            setCurrentPlayingChannel(curChannelIndex);
        }

        int curPipStatus = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.commons_pipstatus_key), -1);
        if (curPipStatus != -1) {
            int requestCode = curPipStatus == 1 ? getResources().getInteger(R.integer.requestcode_pipdeactivate) : getResources().getInteger(R.integer.requestcode_pipactivate);
            DownloadTask d = new DownloadTask("showPip=" + curPipStatus, requestCode, getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }

        String curPipChannel = SharedPrefs.getString(getApplicationContext(), filename, getString(R.string.commons_pipchannel_key), "");
        if (!curPipChannel.isEmpty() && curPipStatus == 1) {
            DownloadTask d = new DownloadTask("channelPip=" + curPipChannel, getResources().getInteger(R.integer.requestcode_pipactivate), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }

        int curVolume = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.commons_volume_key), volume);
        if (curVolume != -1) {
            volume = curVolume;
            DownloadTask d = new DownloadTask("volume=" + volume, getResources().getInteger(R.integer.requestcode_volume_commons), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }

        int curMuted = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.commons_volume_muted_key), muted);
        if (curMuted != -1) {
            muted = curMuted;
            DownloadTask d = new DownloadTask("volume=" + (muted == 1 ? 0 : volume), getResources().getInteger(R.integer.requestcode_mute_commons), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }
    }

    public void updateFavStatus(Boolean isFav) {
        ImageButton btnPlayingFavorite = findViewById(R.id.btnPlayingFavorite);
        if (isFav) {
            btnPlayingFavorite.setImageResource(R.drawable.fav_fill_reverse_black_anim);
        } else {
            btnPlayingFavorite.setImageResource(R.drawable.fav_fill_black_anim);
        }
    }

    private void setButtonEnabled(final ImageButton b, Boolean enabled) {
        b.setEnabled(enabled);
        if (enabled) {
            b.clearColorFilter();
            b.getBackground().clearColorFilter();
            b.setElevation(getResources().getDimension(R.dimen.control_elevation_material));
        } else {
            b.setColorFilter(0xffc8c8c8);    // Button ausgrauen
            b.getBackground().setColorFilter(0xfff0f0f0, PorterDuff.Mode.SRC_IN);
            b.setElevation(0);
        }
    }

    private void onVolumeChanged() {
        SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_volume_key), volume);

        final ImageButton btnVolumeUp = findViewById(R.id.btnVolumeUp);
        setButtonEnabled(btnVolumeUp, volume != 100);
        final ImageButton btnVolumeDown = findViewById(R.id.btnVolumeDown);
        setButtonEnabled(btnVolumeDown, volume != 0);

        if (muted == 1) {
            muted = 0;
            onMutedChanged();
        }
    }

    private void onMutedChanged() {
        SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_volume_muted_key), muted);

        final ImageView btnMute = findViewById(R.id.btnMute);
        if (muted == 1) {
            btnMute.setImageResource(R.drawable.ic_volume_off_black_36dp);
        } else {
            btnMute.setImageResource(R.drawable.ic_volume_up_black_36dp);
        }
    }

    private void onTimeshiftPaused() {
        final ImageButton btnPause = findViewById(R.id.btnPause);
        btnPause.setImageResource(R.drawable.ic_play_arrow_black_36dp);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pausedTime++;
            }
        }, 0, 1000);
        play = true;
    }

    private void onTimeshiftResumed() {
        final ImageButton btnPause = findViewById(R.id.btnPause);
        btnPause.setImageResource(R.drawable.ic_pause_black_36dp);

        // Restart pausedTime and destroy Timer object
        pausedTime=0;
        play = false;
        timer.cancel();
        // New Timer object
        timer = new Timer();
    }

    public void setCurrentPlayingChannel(int index) {
        Channel channel = channelManager.getChannelAt(index);
        SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_channelindex_key), index);
        TextView lblPlaying = findViewById(R.id.lblPlaying);
        lblPlaying.setText(channel.getProgram());
        updateFavStatus(channel.getIsFav());

        ImageView imgCurrentChannel = findViewById(R.id.imgCurrentChannel);
        try (InputStream ims = getAssets().open(ActivitySwitchedOn.channelIconFilenames.get(channel.getProgram()))) {
            Drawable d = Drawable.createFromStream(ims, null);
            imgCurrentChannel.setImageDrawable(d);
        } catch (IOException e) {
            Log.e("setCurrentPlayingChannel", e.getMessage());
        }

        if (index != channelPosition && play && pausedTime > 0) {
            // wenn Kanal gewechselt wird, während Timeshift, beende Timeshift
            timeshiftResume();
        }

        curPlayingChannel = channel;
        channelPosition = index;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int reqChooseChannel = getResources().getInteger(R.integer.activitycode_mainchannel);
        int reqChoosePip = getResources().getInteger(R.integer.activitycode_choosepip);

        if ((requestCode == reqChoosePip || requestCode == reqChooseChannel) && resultCode == Activity.RESULT_OK) {
            // für Favoriten-Speicherung
            // TODO: Öfter prüfen? Performance-Probleme?
            ActivitySwitchedOn.channelManager.saveToJSON(getApplicationContext());
        }

        if (requestCode == reqChooseChannel && resultCode == Activity.RESULT_OK) {
            // Hauptkanal
            int channelManagerIndex = data.getIntExtra(getString(R.string.intentExtra_channelAdapterPosition_key), 0);
            Channel channelInstance = ActivitySwitchedOn.channelManager.getChannelAt(channelManagerIndex);
            DownloadTask d = new DownloadTask("channelMain=" + channelInstance.getChannel(), getResources().getInteger(R.integer.requestcode_mainchannel), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();

            // Daten von aktuellem Main-Channel anzeigen
            setCurrentPlayingChannel(channelManagerIndex);
        } else if (requestCode == reqChoosePip && resultCode == Activity.RESULT_OK) {
            // Pip ausgewählt
            int channelAdapterPosition = data.getIntExtra(getString(R.string.intentExtra_channelAdapterPosition_key), 0);
            Channel channelInstance = ActivitySwitchedOn.channelManager.getChannelAt(channelAdapterPosition);

            DownloadTask d = new DownloadTask("showPip=1&channelPip=" + channelInstance.getChannel(), getResources().getInteger(R.integer.requestcode_pipactivate), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
            SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipchannel_key), channelInstance.getChannel());
        }
    }

    @Override
    public void onDownloadTaskCompleted(int requestCode, Boolean success, JSONObject jsonObj) {
        if (!success) {
            if (requestCode == getResources().getInteger(R.integer.requestcode_connectiontest)) {
                // wenn Verbindungstest nicht erfolgreich
                Toast.makeText(getApplicationContext(), "Verbindung zu TV verloren!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ActivitySwitchedOn.this, ActivitySwitchedOff.class));
            } else {
                // wenn nicht erfolgreich, prüfe Verbindung
                Context c = getApplicationContext();
                ActivitySettings.testConnection(ActivitySettings.getIP(c), c, ActivitySwitchedOn.this);
            }
        } else {
            // siehe /res/values/integers.xml für requestCode-Werte

            if (requestCode == getResources().getInteger(R.integer.requestcode_pipactivate)) {
                // PiP aktiviert, setze Button-Farbe auf grün
                ImageButton btnPip = findViewById(R.id.btnPipToggle);
                btnPip.getBackground().setColorFilter(getColor(R.color.colorValidBackground), PorterDuff.Mode.SRC_IN);
                final ImageButton btnPipChange = findViewById(R.id.btnPipChange);
                setButtonEnabled(btnPipChange, true);
                SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipstatus_key), 1);
            } else if (requestCode == getResources().getInteger(R.integer.requestcode_pipdeactivate)) {
                // PiP deaktiviert
                ImageButton btnPip = findViewById(R.id.btnPipToggle);
                btnPip.getBackground().clearColorFilter();
                final ImageButton btnPipChange = findViewById(R.id.btnPipChange);
                setButtonEnabled(btnPipChange, false);
                SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipstatus_key), 0);
            } else if (requestCode == getResources().getInteger(R.integer.requestcode_volume_up)) {
                // Lautstärke hoch
                volume++;
                onVolumeChanged();
            } else if (requestCode == getResources().getInteger(R.integer.requestcode_volume_down)) {
                // Lautstärke runter
                volume--;
                onVolumeChanged();
            } else if (requestCode == getResources().getInteger(R.integer.requestcode_volume_commons)) {
                // Lautstärke aus Commons geladen
                onVolumeChanged();
            } else if (requestCode == getResources().getInteger(R.integer.requestcode_mute_change)) {
                // Mute-Status manuell geändert
                muted = (muted == 0 ? 1 : 0);
                onMutedChanged();
            } else if (requestCode == getResources().getInteger(R.integer.requestcode_mute_commons)) {
                // Mute-Status aus Commons abgerufen
                onMutedChanged();
            } else if (requestCode == getResources().getInteger(R.integer.requestcode_timeshift_pause)) {
                // Pausiert
                onTimeshiftPaused();
            } else if (requestCode == getResources().getInteger(R.integer.requestcode_timeshift_resume)) {
                // Fortgesetzt & timeShiftPlay=offset gesetzt
                onTimeshiftResumed();
            } else if (requestCode == 99) {
                // Power-Button gedrückt, gehe zu ActivitySwitchedOff
                SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_standbystate_key), 1);
                startActivity(new Intent(ActivitySwitchedOn.this, ActivitySwitchedOff.class));
            }
        }
    }
}
