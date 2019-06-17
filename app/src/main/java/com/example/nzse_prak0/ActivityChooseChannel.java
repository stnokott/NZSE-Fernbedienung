package com.example.nzse_prak0;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nzse_prak0.helpers.Channel;
import com.example.nzse_prak0.helpers.OnDownloadTaskCompleted;
import com.example.nzse_prak0.helpers.RequestTask;
import com.example.nzse_prak0.helpers.TileAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityChooseChannel extends AppCompatActivity implements OnDownloadTaskCompleted {
    private Menu mMenu;
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

        GridLayoutManager gridLayoutManager;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 4);
        } else {
            gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 2);
        }
        recyclerView.setLayoutManager(gridLayoutManager);

        initTileAdapter();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        createListeners(tileAdapter);
        checkIntentExtras();
        checkNoChannelsVisible();
    }

    private void initTileAdapter() {
        List<Channel> channels = new ArrayList<>(ActivitySwitchedOn.CHANNEL_MANAGER.getChannels());

        tileAdapter = new TileAdapter(channels);
        tileAdapter.setHasStableIds(false);
        ((RecyclerView) findViewById(R.id.recyclerViewChannel)).setAdapter(tileAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
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
                toggleFavsOnly();
                return false;
            }
        });

        return true;
    }

    private void toggleFavsOnly() {
        favsOnly = !favsOnly;

        final MenuItem btnToggleFavs = mMenu.findItem(R.id.btnToggleFavs);
        if (favsOnly) {
            btnToggleFavs.setIcon(getDrawable(R.drawable.ic_favorite_white_36dp));
            tileAdapter.setChannelList(ActivitySwitchedOn.CHANNEL_MANAGER.getFavoriteChannels());
        } else {
            btnToggleFavs.setIcon(getDrawable(R.drawable.ic_favorite_border_white_36dp));
            tileAdapter.setChannelList(ActivitySwitchedOn.CHANNEL_MANAGER.getChannels());
        }
    }

    private void showSnack(String text, int duration, @Nullable String actionText, @Nullable View.OnClickListener actionListener) {
        final CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayoutChooseChannel);
        Snackbar snack = Snackbar.make(coordinatorLayout, text, duration);
        if (actionText != null && actionListener != null) {
            snack.setAction(actionText, actionListener);
        }
        snack.show();
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

                String curChannelName = viewHolder.getChannelTile().getChannelInstance().getChannelId();
                // um auch bei gefilterter RecyclerView (z.B. favOnly) den korrekten Index zu bekommen
                int curChannelAdapterIndex = ActivitySwitchedOn.CHANNEL_MANAGER.getIndexFromChannelName(curChannelName);
                returnIntent.putExtra(getString(R.string.intentExtra_channelAdapterPosition_key), curChannelAdapterIndex);

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
                onBtnScanChannels();
            }
        });
    }

    private void onBtnScanChannels() {
        if (ActivitySwitchedOn.CHANNEL_MANAGER.getChannelCount() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityChooseChannel.this);
            builder.setMessage(getString(R.string.lblAlertScanWarning)).setTitle(getString(R.string.titleAlertScanWarning));
            builder.setPositiveButton(getString(R.string.lblAlertConfirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scanChannels();
                }
            });
            builder.setNegativeButton(getString(R.string.lblAlertDeny), null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            scanChannels();
        }
    }

    private void scanChannels() {
        RequestTask d = new RequestTask("scanChannels=", getResources().getInteger(R.integer.requestcode_scanchannels), getApplicationContext(), ActivityChooseChannel.this);
        d.execute();
        showSnack(getString(R.string.lblWaiting), Snackbar.LENGTH_SHORT, null, null);

        findViewById(R.id.btnScanChannels).setVisibility(View.INVISIBLE);
        findViewById(R.id.recyclerViewChannel).setEnabled(false);
        findViewById(R.id.spinnerOverlay).setVisibility(View.VISIBLE);
        findViewById(R.id.backgroundOverlay).setVisibility(View.VISIBLE);

        if (favsOnly) {
            toggleFavsOnly();
        }
    }

    private void checkIntentExtras() {
        // falls beim Starten der Aktivität Kanalscan durchgeführt werden soll
        if (getIntent().hasExtra(getString(R.string.intentExtra_doChannelscanOnEnter_key)))
            onBtnScanChannels();
    }

    @Override
    public void onDownloadTaskCompleted(int requestCode, Boolean success, JSONObject jsonObj) {
        if (requestCode == getResources().getInteger(R.integer.requestcode_scanchannels)) {
            if (success && jsonObj.length() > 0) {
                try {
                    ActivitySwitchedOn.CHANNEL_MANAGER.parseJSON(jsonObj);
                    ActivitySwitchedOn.CHANNEL_MANAGER.saveToJSON(getApplicationContext());
                    tileAdapter.setChannelList(ActivitySwitchedOn.CHANNEL_MANAGER.getChannels());
                    if (getIntent().hasExtra(getString(R.string.intentExtra_doChannelscanOnEnter_key))) {
                        // falls Aktivität nur für Kanalscan gestartet wurde, kann hier beendet werden
                        finish();
                    }
                    showSnack(getString(R.string.lblScanSuccess), Snackbar.LENGTH_SHORT, null, null);
                } catch (JSONException e) {
                    Log.e("onDownloadTaskCompleted", e.getMessage());
                }
            } else {
                showSnack(getString(R.string.lblScanFailure), Snackbar.LENGTH_SHORT, null, null);
            }
        }

        findViewById(R.id.btnScanChannels).setVisibility(View.VISIBLE);
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
