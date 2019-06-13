package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ClipDrawable;
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
import com.example.nzse_prak0.helpers.OnDownloadTaskCompleted;
import com.example.nzse_prak0.helpers.RequestTask;
import com.example.nzse_prak0.helpers.SharedPrefs;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ActivitySwitchedOn extends AppCompatActivity implements OnDownloadTaskCompleted {
    public static final ChannelManager channelManager = new ChannelManager();

    private static final String CHANNEL_ICON_FILENAMES_DICT_FILE = "filenames.json";
    public static final Map<String, String> channelIconFilenames = new HashMap<>();
    private boolean play = true;
    private int pausedTime = 0;
    private int volume = 50;
    private int muted = 0;
    private Timer timer =new Timer();
    private int currentChannelIndex = -1;
    private int currentPipIndex = -1;

    private int showBackgroundImg = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_on);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_settings_white_36dp);
        final ImageButton btnPipChange = findViewById(R.id.btnPipChange);
        //setButtonEnabled(btnPipChange, false);
        btnPipChange.setVisibility(View.GONE);

        channelManager.loadFromJSON(getApplicationContext());
        loadIconFilenamesFromJSON();
        applyLastKnownSettings();


        // Start debug mode and shows a status bar at the bottom
        RequestTask d = new RequestTask("debug=1", 0, getApplicationContext(), null);
        d.execute();
        // Timeshift beenden, falls noch läuft
        RequestTask d1 = new RequestTask("timeShiftPlay=0", getResources().getInteger(R.integer.requestcode_timeshift_resume), getApplicationContext(), null);
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

        if (id == R.id.btnSettings) {
            startActivityForResult(new Intent(ActivitySwitchedOn.this, ActivitySettings.class), getResources().getInteger(R.integer.activitycode_settings));
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
                RequestTask d = new RequestTask("standby=1", getResources().getInteger(R.integer.requestcode_standby), getApplicationContext(), ActivitySwitchedOn.this);
                d.execute();
            }
        });

        final Button btnChannels = findViewById(R.id.btnChannels);
        btnChannels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class), getResources().getInteger(R.integer.activitycode_choosechannel));
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
                    pipDeactivate();
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
                if (currentChannelIndex != -1)
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
            volume++;
            onVolumeChanged();

            RequestTask d = new RequestTask("volume=" + volume, getResources().getInteger(R.integer.requestcode_volume_up), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }
    }

    private void volumeDown() {
        if (volume > 0) {
            volume--;
            onVolumeChanged();

            RequestTask d = new RequestTask("volume=" + volume, getResources().getInteger(R.integer.requestcode_volume_down), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }
    }

    private void volumeToggleMute() {
        muted = (muted == 0 ? 1 : 0);
        onMutedChanged();

        RequestTask d = new RequestTask("volume=" + (muted == 0 ? 0 : volume), getResources().getInteger(R.integer.requestcode_mute_change), getApplicationContext(), ActivitySwitchedOn.this);
        d.execute();
    }

    private void nextChannel() {
        int nextIndex;
        if (currentChannelIndex == channelManager.getChannelCount() - 1) {
            // falls Ende erreicht, fange von vorne an
            nextIndex = 0;
        } else {
            nextIndex = currentChannelIndex + 1;
        }
        Channel channel = channelManager.getChannelAt(nextIndex);
        RequestTask d = new RequestTask("channelMain=" + channel.getChannelId(), getResources().getInteger(R.integer.requestcode_channel_change), getApplicationContext(), null);
        d.execute();

        setCurrentPlayingChannel(nextIndex);
    }

    private void previousChannel() {
        int nextIndex;
        if (currentChannelIndex == 0) {
            // falls Anfang erreicht, fange von hinten an
            nextIndex = channelManager.getChannelCount() - 1;
        } else {
            nextIndex = currentChannelIndex - 1;
        }
        Channel channel = channelManager.getChannelAt(nextIndex);
        RequestTask d = new RequestTask("channelMain=" + channel.getChannelId(), getResources().getInteger(R.integer.requestcode_channel_change), getApplicationContext(), null);
        d.execute();

        setCurrentPlayingChannel(nextIndex);
    }

    private void timeshiftPause() {
        onTimeshiftPaused();

        RequestTask d = new RequestTask("timeShiftPause=", getResources().getInteger(R.integer.requestcode_timeshift_pause), getApplicationContext(), ActivitySwitchedOn.this);
        d.execute();
    }

    private void timeshiftResume() {
        onTimeshiftResumed();

        RequestTask d = new RequestTask("timeShiftPlay=" + pausedTime, getResources().getInteger(R.integer.requestcode_timeshift_resume), getApplicationContext(), ActivitySwitchedOn.this);
        d.execute();
    }

    private void toggleFavButton() {
        Boolean isFav = channelManager.getChannelAt(currentChannelIndex).getIsFav();
        updateCurPlayingFavStatus(!isFav, true);

        channelManager.getChannelAt(currentChannelIndex).setIsFav(!isFav);
        channelManager.saveToJSON(getApplicationContext());
    }

    private void pipActivate() {
        // setze Button-Farbe auf grün
        final ImageButton btnPip = findViewById(R.id.btnPipToggle);
        btnPip.getBackground().setColorFilter(getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        final ImageButton btnPipChange = findViewById(R.id.btnPipChange);
        //setButtonEnabled(btnPipChange, true);
        btnPipChange.setVisibility(View.VISIBLE);

        if (currentPipIndex != -1) {
            Channel pipChannel = channelManager.getChannelAt(currentPipIndex);
            try (InputStream ims = getAssets().open(channelIconFilenames.get(pipChannel.getProgram()))) {
                Drawable drawable = Drawable.createFromStream(ims, null);
                drawable.setAlpha(150);
                btnPipChange.setImageDrawable(drawable);
            } catch (IOException e) {
                btnPipChange.setImageResource(R.drawable.ic_swap_horiz_black_36dp);
                Log.e("setCurrentPlayingChannel", e.getMessage());
            }
        }

        SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipstatus_key), 1);

        RequestTask d = new RequestTask("showPip=1", getResources().getInteger(R.integer.requestcode_pipactivate), getApplicationContext(), ActivitySwitchedOn.this);
        d.execute();
    }

    private void pipDeactivate() {
        ImageButton btnPip = findViewById(R.id.btnPipToggle);
        btnPip.getBackground().clearColorFilter();
        final ImageButton btnPipChange = findViewById(R.id.btnPipChange);
        //setButtonEnabled(btnPipChange, false);
        btnPipChange.setVisibility(View.GONE);
        SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipstatus_key), 0);

        RequestTask d = new RequestTask("showPip=0", getResources().getInteger(R.integer.requestcode_pipdeactivate), getApplicationContext(), ActivitySwitchedOn.this);
        d.execute();
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

    private void applyLastKnownSettings() {
        String filename = getString(R.string.commons_file_name);

        showBackgroundImg = ActivitySettings.getShowBackgroundImg(getApplicationContext());

        int curChannelIndex = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.commons_channelindex_key), -1);
        if (curChannelIndex != -1 && channelManager.getChannelCount() > curChannelIndex) {
            RequestTask d = new RequestTask("channelMain=" + channelManager.getChannelAt(curChannelIndex).getChannelId(), getResources().getInteger(R.integer.requestcode_mainchannel), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
            setCurrentPlayingChannel(curChannelIndex);
        }

        int curPipStatus = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.commons_pipstatus_key), -1);
        if (curPipStatus != -1) {
            if (curPipStatus == 0) {
                pipDeactivate();
            } else {
                pipActivate();
            }
        }

        int curPipChannel = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.commons_pipchannel_key), -1);
        currentPipIndex = curPipChannel;
        if (curPipChannel != -1 && curPipStatus == 1) {
            RequestTask d = new RequestTask("channelPip=" + curPipChannel, getResources().getInteger(R.integer.requestcode_pipactivate), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }

        int curVolume = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.commons_volume_key), volume);
        if (curVolume != -1) {
            volume = curVolume;
            onVolumeChanged();

            RequestTask d = new RequestTask("volume=" + volume, getResources().getInteger(R.integer.requestcode_volume_commons), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }

        int curMuted = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.commons_volume_muted_key), muted);
        if (curMuted != -1) {
            muted = curMuted;
            onMutedChanged();

            RequestTask d = new RequestTask("volume=" + (muted == 1 ? 0 : volume), getResources().getInteger(R.integer.requestcode_mute_commons), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }

        int zoomMain = ActivitySettings.getZoomMain(getApplicationContext());
        RequestTask d = new RequestTask("zoomMain=" + zoomMain, getResources().getInteger(R.integer.requestcode_zoom_main), getApplicationContext(), ActivitySwitchedOn.this);
        d.execute();

        int zoomPip = ActivitySettings.getZoomPip(getApplicationContext());
        RequestTask d1 = new RequestTask("zoomPip=" + zoomPip, getResources().getInteger(R.integer.requestcode_zoom_pip), getApplicationContext(), ActivitySwitchedOn.this);
        d1.execute();
    }

    public void updateCurPlayingFavStatus(Boolean isFav, boolean animate) {
        ImageButton btnPlayingFavorite = findViewById(R.id.btnPlayingFavorite);
        if (isFav) {
            if (animate) {
                btnPlayingFavorite.setImageResource(R.drawable.ic_favorite_anim_white_36dp);
                Animatable btnPlayingFavoriteAnim = (Animatable) btnPlayingFavorite.getDrawable();
                btnPlayingFavoriteAnim.start();
            } else {
                btnPlayingFavorite.setImageResource(R.drawable.ic_favorite_anim_reverse_white_36dp);
            }
        } else {
            if (animate) {
                btnPlayingFavorite.setImageResource(R.drawable.ic_favorite_anim_reverse_white_36dp);
                Animatable btnPlayingFavoriteAnim = (Animatable) btnPlayingFavorite.getDrawable();
                btnPlayingFavoriteAnim.start();
            } else {
                btnPlayingFavorite.setImageResource(R.drawable.ic_favorite_anim_white_36dp);
            }
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

        final ImageView btnMuteOverlay = findViewById(R.id.btnMuteOverlay);
        ClipDrawable mImageDrawable = (ClipDrawable) btnMuteOverlay.getDrawable();
        mImageDrawable.setLevel(Math.round(10000 * (volume / 100f)));

        if (muted == 1) {
            muted = 0;
            onMutedChanged();
        }
    }

    private void onMutedChanged() {
        SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_volume_muted_key), muted);

        final ImageView btnMute = findViewById(R.id.btnMute);
        final ImageView btnMuteOverlay = findViewById(R.id.btnMuteOverlay);
        ClipDrawable mImageDrawable = (ClipDrawable) btnMuteOverlay.getDrawable();
        if (muted == 1) {
            btnMute.setImageResource(R.drawable.ic_volume_off_black_36dp);
            mImageDrawable.setLevel(0);
        } else {
            btnMute.setImageResource(R.drawable.ic_volume_up_black_36dp);
            mImageDrawable.setLevel(Math.round(10000 * (volume / 100f)));
        }
    }

    private void onTimeshiftPaused() {
        int orientation = getResources().getConfiguration().orientation;
        final ImageButton btnPause = findViewById(R.id.btnPause);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            btnPause.setImageResource(R.drawable.ic_play_arrow_black_nopadding_36dp);
        } else {
            btnPause.setImageResource(R.drawable.ic_play_arrow_black_36dp);
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pausedTime++;
            }
        }, 0, 1000);
        play = true;
    }

    private void onTimeshiftResumed() {
        int orientation = getResources().getConfiguration().orientation;
        final ImageButton btnPause = findViewById(R.id.btnPause);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            btnPause.setImageResource(R.drawable.ic_pause_black_nopadding_36dp);
        } else {
            btnPause.setImageResource(R.drawable.ic_pause_black_36dp);
        }

        // Restart pausedTime and destroy Timer object
        pausedTime = 0;
        play = false;
        timer.cancel();
        // New Timer object
        timer = new Timer();
    }

    public void setCurrentPlayingChannel(int index) {
        if (index < 0 || index >= channelManager.getChannelCount()) {
            return;
        }
        Channel channel = channelManager.getChannelAt(index);
        SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_channelindex_key), index);
        TextView lblPlaying = findViewById(R.id.lblPlaying);
        lblPlaying.setText(channel.getProgram());
        updateCurPlayingFavStatus(channel.getIsFav(), false);

        ImageView imgCurrentChannel = findViewById(R.id.imgCurrentChannel);
        if (showBackgroundImg == 1) {
            try (InputStream ims = getAssets().open(ActivitySwitchedOn.channelIconFilenames.get(channel.getProgram()))) {
                Drawable d = Drawable.createFromStream(ims, null);
                imgCurrentChannel.setImageDrawable(d);
            } catch (IOException e) {
                Log.e("setCurrentPlayingChannel", e.getMessage());
            }
        } else {
            imgCurrentChannel.setImageDrawable(null);
        }

        if (index != currentChannelIndex && play && pausedTime > 0) {
            // wenn Kanal gewechselt wird, während Timeshift, beende Timeshift
            timeshiftResume();
        }

        currentChannelIndex = index;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int reqChooseChannel = getResources().getInteger(R.integer.activitycode_choosechannel);
        int reqChoosePip = getResources().getInteger(R.integer.activitycode_choosepip);
        int reqSettings = getResources().getInteger(R.integer.activitycode_settings);

        if (requestCode == reqChoosePip || requestCode == reqChooseChannel) {
            // für Favoriten-Speicherung
            ActivitySwitchedOn.channelManager.saveToJSON(getApplicationContext());
            setCurrentPlayingChannel(currentChannelIndex); // Favo-Status aktualisieren
        }

        if (requestCode == reqChooseChannel && resultCode == Activity.RESULT_OK) {
            // Hauptkanal
            int channelManagerIndex = data.getIntExtra(getString(R.string.intentExtra_channelAdapterPosition_key), 0);
            Channel channelInstance = ActivitySwitchedOn.channelManager.getChannelAt(channelManagerIndex);
            RequestTask d = new RequestTask("channelMain=" + channelInstance.getChannelId(), getResources().getInteger(R.integer.requestcode_mainchannel), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();

            // Daten von aktuellem Main-Channel anzeigen
            setCurrentPlayingChannel(channelManagerIndex);
        } else if (requestCode == reqChoosePip && resultCode == Activity.RESULT_OK) {
            // Pip ausgewählt
            int channelAdapterPosition = data.getIntExtra(getString(R.string.intentExtra_channelAdapterPosition_key), 0);
            currentPipIndex = channelAdapterPosition;
            Channel channelInstance = ActivitySwitchedOn.channelManager.getChannelAt(channelAdapterPosition);

            RequestTask d = new RequestTask("showPip=1&channelPip=" + channelInstance.getChannelId(), getResources().getInteger(R.integer.requestcode_pipactivate), getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
            SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipchannel_key), channelAdapterPosition);

            pipActivate();
        } else if (requestCode == reqSettings && resultCode == Activity.RESULT_OK) {
            // Einstellungen bestätigt
            int newShowBackgroundImg = data.getIntExtra(getString(R.string.intentExtra_showBackgroundImg_key), 1);
            if (newShowBackgroundImg != showBackgroundImg) {
                showBackgroundImg = newShowBackgroundImg;
                setCurrentPlayingChannel(currentChannelIndex);
            }
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

            if (requestCode == 99) {
                // Power-Button gedrückt, gehe zu ActivitySwitchedOff
                SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_standbystate_key), 1);
                startActivity(new Intent(ActivitySwitchedOn.this, ActivitySwitchedOff.class));
            }
        }
    }
}
