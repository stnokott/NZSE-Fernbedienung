package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
    // TODO: ApplicationContext global verfügbar machen (und Kontext-Übergaben redundant machen): https://stackoverflow.com/a/5114361
    public static final ChannelManager channelManager = new ChannelManager();
    private Channel curPlayingChannel = null;

    private static final String CHANNEL_ICON_FILENAMES_DICT_FILE = "filenames.json";
    public static HashMap<String, String> channelIconFilenames = new HashMap<>();
    private boolean play = true;
    private int pausedTime = 0;
    private Timer T=new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_on);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_settings_white_36dp);
        setBtnPipChangeEnabled(false);

        channelManager.loadFromJSON(getApplicationContext());
        loadIconFilenamesFromJSON();
        applyLastKnownCommons();


        // Start debug mode and shows a status bar at the bottom
        DownloadTask d = new DownloadTask("debug=1", 0, getApplicationContext(), null);
        d.execute();


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
                DownloadTask d = new DownloadTask("standby=1", 9, getApplicationContext(), ActivitySwitchedOn.this);
                d.execute();
            }
        });

        final Button btnChannels = findViewById(R.id.btnChannels);
        btnChannels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectChannelIntent = new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class);
                selectChannelIntent.putExtra("favsOnly", 0);
                startActivityForResult(selectChannelIntent, 1);
            }
        });

        final ImageButton btnFavs = findViewById(R.id.btnFavs);
        btnFavs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent selectChannelIntent = new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class);
                selectChannelIntent.putExtra("favsOnly", 1);
                startActivityForResult(selectChannelIntent, 1);
            }
        });

        final ImageButton btnPause = findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if( play && pausedTime > 0){
                    btnPause.setImageResource(R.drawable.ic_pause_black_36dp);
                    DownloadTask d = new DownloadTask("timeShiftPlay=" + pausedTime, 5, getApplicationContext(),null);
                    d.execute();

                    // Restart pausedTime and destroy Timer object
                    pausedTime=0;
                    play = false;
                    T.cancel();
                    // New Timer object
                    T = new Timer();
                } else{
                    T.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            pausedTime++;
                        }
                    }, 0, 1000);
                    btnPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    DownloadTask d = new DownloadTask("timeShiftPause=",5, getApplicationContext(),null);
                    d.execute();
                    play = true;
            }
            }
        });

        final ImageButton btnPip = findViewById(R.id.btnPip);
        btnPip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pipstatus = SharedPrefs.getInt(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipstatus_key), 0);
                if (pipstatus == 1) {
                    // wenn Pip schon aktiviert, deaktiviere
                    DownloadTask d = new DownloadTask("showPip=0", 4, getApplicationContext(), ActivitySwitchedOn.this);
                    d.execute();
                } else {
                    // wenn Pip noch nicht aktiviert, aktiviere und wähle Kanal
                    startActivityForResult(new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class), 3);
                }
            }
        });

        final ImageButton btnPipSwap = findViewById(R.id.btnPipChange);
        btnPipSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pipstatus = SharedPrefs.getInt(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipstatus_key), 0);
                if (pipstatus == 1) {
                    startActivityForResult(new Intent(ActivitySwitchedOn.this, ActivityChooseChannel.class), 3);
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
    }

    public void toggleFavButton() {
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
        // TODO: Einstellung hinzufügen, ob zuletzt bekannte Commons bei Start angewendet werden sollen?
        String filename = getString(R.string.commons_file_name);
        int curChannelIndex = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.commons_channelindex_key), -1);
        if (curChannelIndex != -1 && channelManager.getChannelCount() > curChannelIndex) {
            DownloadTask d = new DownloadTask("channelMain=" + channelManager.getChannelAt(curChannelIndex).getChannel(), 1, getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
            setCurrentPlayingChannel(curChannelIndex);
        }

        int curPipStatus = SharedPrefs.getInt(getApplicationContext(), filename, getString(R.string.commons_pipstatus_key), -1);
        if (curPipStatus != -1) {
            int requestCode = curPipStatus == 1 ? 3 : 4;
            DownloadTask d = new DownloadTask("showPip=" + curPipStatus, requestCode, getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }

        String curPipChannel = SharedPrefs.getString(getApplicationContext(), filename, getString(R.string.commons_pipchannel_key), "");
        if (!curPipChannel.isEmpty() && curPipStatus == 1) {
            DownloadTask d = new DownloadTask("channelPip=" + curPipChannel, 5, getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
        }
    }

    public void updateFavStatus(Boolean isFav) {
        ImageButton btnPlayingFavorite = findViewById(R.id.btnPlayingFavorite);
        if (isFav) {
            btnPlayingFavorite.setImageResource(R.drawable.star_fill_reverse_black_anim);
        } else {
            btnPlayingFavorite.setImageResource(R.drawable.star_fill_black_anim);
        }
    }

    private void setBtnPipChangeEnabled(Boolean enabled) {
        final ImageButton btnPipChange = findViewById(R.id.btnPipChange);
        btnPipChange.setEnabled(enabled);
        if (enabled) {
            btnPipChange.clearColorFilter();
            btnPipChange.getBackground().clearColorFilter();
            btnPipChange.setElevation(getResources().getDimension(R.dimen.control_elevation_material));
        } else {
            btnPipChange.setColorFilter(0xffc8c8c8);    // Button ausgrauen
            btnPipChange.getBackground().setColorFilter(0xfff0f0f0, PorterDuff.Mode.SRC_IN);
            btnPipChange.setElevation(0);
        }
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

        curPlayingChannel = channel;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
            requestCode:
            1 = Channel-Auswahl
            3 = PiP aktivieren
         */
        if ((requestCode == 3 || requestCode == 1) && resultCode == Activity.RESULT_OK) {
            // für Favoriten-Speicherung
            // TODO: Öfter prüfen? Performance-Probleme?
            ActivitySwitchedOn.channelManager.saveToJSON(getApplicationContext());
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Hauptkanal
            int channelManagerIndex = data.getIntExtra(getString(R.string.intentExtra_channelAdapterPosition_key), 0);
            Channel channelInstance = ActivitySwitchedOn.channelManager.getChannelAt(channelManagerIndex);
            DownloadTask d = new DownloadTask("channelMain=" + channelInstance.getChannel(), 1, getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();

            // Daten von aktuellem Main-Channel anzeigen
            setCurrentPlayingChannel(channelManagerIndex);
        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            // Pip ausgewählt
            int channelAdapterPosition = data.getIntExtra(getString(R.string.intentExtra_channelAdapterPosition_key), 0);
            Channel channelInstance = ActivitySwitchedOn.channelManager.getChannelAt(channelAdapterPosition);

            DownloadTask d = new DownloadTask("showPip=1&channelPip=" + channelInstance.getChannel(), 3, getApplicationContext(), ActivitySwitchedOn.this);
            d.execute();
            SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipchannel_key), channelInstance.getChannel());
        }
    }

    @Override
    public void onDownloadTaskCompleted(int requestCode, Boolean success, JSONObject jsonObj) {
        /*
            requestCode:
            1 = Hauptkanal wählen
            3 = PiP aktivieren und Kanal wählen
            4 = PiP deaktivieren
            9 = Standby aktivieren
         */
        if (requestCode == 3 && success) {
            // PiP aktiviert, setze Button-Farbe auf grün
            ImageButton btnPip = findViewById(R.id.btnPipToggle);
            btnPip.getBackground().setColorFilter(getColor(R.color.colorValidBackground), PorterDuff.Mode.SRC_IN);
            setBtnPipChangeEnabled(true);
            SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipstatus_key), 1);
        } else if (requestCode == 4 && success) {
            // PiP deaktiviert
            ImageButton btnPip = findViewById(R.id.btnPipToggle);
            btnPip.getBackground().clearColorFilter();
            setBtnPipChangeEnabled(false);
            SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_pipstatus_key), 0);
        } else if (requestCode == 9 && success) {
            // Power-Button gedrückt, gehe zu ActivitySwitchedOff
            SharedPrefs.setValue(getApplicationContext(), getString(R.string.commons_file_name), getString(R.string.commons_standbystate_key), 1);
            startActivity(new Intent(ActivitySwitchedOn.this, ActivitySwitchedOff.class));
        }
    }
}
