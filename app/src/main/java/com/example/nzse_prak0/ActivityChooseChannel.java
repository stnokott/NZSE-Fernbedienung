package com.example.nzse_prak0;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nzse_prak0.helpers.Channel;
import com.example.nzse_prak0.helpers.DownloadTask;
import com.example.nzse_prak0.helpers.OnDownloadTaskCompleted;
import com.example.nzse_prak0.helpers.TileAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityChooseChannel extends AppCompatActivity implements OnDownloadTaskCompleted {
    private TileAdapter tileAdapter;
    private SearchView btnSearch;
    boolean favsOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosechannel);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewChannel);
        recyclerView.setHasFixedSize(true); // bessere Performance, wenn Layout-Größe sich nicht ändert
        recyclerView.setItemViewCacheSize(25);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        initTileAdapter();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        createListeners(tileAdapter);
        checkNoChannelsVisible();
    }

    private void initTileAdapter() {
        List<Channel> channels = new ArrayList<>(ActivitySwitchedOn.channelManager.getChannels());

        tileAdapter = new TileAdapter(channels);
        tileAdapter.setHasStableIds(false);
        ((RecyclerView) findViewById(R.id.recyclerViewChannel)).setAdapter(tileAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choosechannel, menu);

        btnSearch = (SearchView) menu.findItem(R.id.btnSearch).getActionView();
        btnSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tileAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tileAdapter.getFilter().filter(newText);
                return false;
            }
        });

        final MenuItem btnToggleFavs = menu.findItem(R.id.btnToggleFavs);
        btnToggleFavs.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                favsOnly = !favsOnly;

                if (favsOnly) {
                    btnToggleFavs.setIcon(getDrawable(R.drawable.ic_favorite_white_36dp));
                    tileAdapter.setChannelList(ActivitySwitchedOn.channelManager.getFavoriteChannels());
                } else {
                    btnToggleFavs.setIcon(getDrawable(R.drawable.ic_favorite_border_white_36dp));
                    tileAdapter.setChannelList(ActivitySwitchedOn.channelManager.getChannels());
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!btnSearch.isIconified()) {
            btnSearch.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void createListeners(final TileAdapter tileAdapter) {
        // Listener für Tiles
        tileAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TileAdapter.TileViewHolder viewHolder = (TileAdapter.TileViewHolder) v.getTag();

                Intent returnIntent = new Intent();
                returnIntent.putExtra(getString(R.string.intentExtra_channelAdapterPosition_key), viewHolder.getAdapterPosition());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        tileAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkNoChannelsVisible();
            }
        });

        final FloatingActionButton btnScanChannels = findViewById(R.id.btnScanChannels);
        btnScanChannels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favsOnly) {
                    DownloadTask d = new DownloadTask("scanChannels=", getResources().getInteger(R.integer.requestcode_scanchannels), getApplicationContext(), ActivityChooseChannel.this);
                    d.execute();
                    Toast t = Toast.makeText(getApplicationContext(), "Bitte warten...", Toast.LENGTH_SHORT);
                    t.show();

                    btnScanChannels.setEnabled(false);
                    findViewById(R.id.recyclerViewChannel).setEnabled(false);
                    findViewById(R.id.spinnerOverlay).setVisibility(View.VISIBLE);
                    findViewById(R.id.backgroundOverlay).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onDownloadTaskCompleted(int requestCode, Boolean success, JSONObject jsonObj) {
        if (requestCode == getResources().getInteger(R.integer.requestcode_scanchannels) && success) {
            Toast toast = Toast.makeText(getApplicationContext(), "Kanäle gescannt!", Toast.LENGTH_SHORT);
            toast.show();
            try {
                ActivitySwitchedOn.channelManager.parseJSON(jsonObj);
                ActivitySwitchedOn.channelManager.saveToJSON(getApplicationContext());
                tileAdapter.setChannelList(ActivitySwitchedOn.channelManager.getChannels());
            } catch (JSONException e) {
                Log.e("onDownloadTaskCompleted", e.getMessage());
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Fehler beim Channel-Scan!", Toast.LENGTH_SHORT);
            toast.show();
        }

        findViewById(R.id.btnScanChannels).setEnabled(true);
        findViewById(R.id.recyclerViewChannel).setEnabled(true);
        findViewById(R.id.spinnerOverlay).setVisibility(View.INVISIBLE);
        findViewById(R.id.backgroundOverlay).setVisibility(View.INVISIBLE);
    }

    public void checkNoChannelsVisible() {
        TextView txtNoChannels = findViewById(R.id.lblNoChannels);
        txtNoChannels.setVisibility(tileAdapter.getItemCount()==0 ? View.VISIBLE:View.INVISIBLE);
        if (favsOnly) {
            txtNoChannels.setText(getString(R.string.lblNoFavorites));
        } else {
            txtNoChannels.setText(getString(R.string.lblNoChannels));
        }
    }
}
