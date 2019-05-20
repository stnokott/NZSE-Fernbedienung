package com.example.nzse_prak0.helpers;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;

import com.example.nzse_prak0.customviews.ChannelTile;

import java.util.List;

public class ChannelTileManager {
    private int nextId = 0;
    private Context context;
    private TableLayout tl;
    private List<ChannelTile> tiles;

    public ChannelTileManager(TableLayout tl) {
        this.context = tl.getContext();
        this.tl = tl;
    }

    public void addTile(String title) {
        // TODO: TileRow-Klasse schreiben, die Guideline- und Tile-Attribute hat, um unsichere Aufrufe zu getChildrenByClass zu vermeiden
        ChannelTile newTile = new ChannelTile(context, title, "0", Color.RED);
        ConstraintSet cset = new ConstraintSet();
        cset.connect(newTile.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        cset.connect(newTile.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

        if (tiles.size() % 2 == 0 || tiles.isEmpty()) {
            // neue Reihe anlegen
            ConstraintLayout cl = addRow();
            cl.addView(newTile);

            Guideline gl = (Guideline) LayoutHelper.getChildrenByClass(cl, Guideline.class).get(0);

            cset.connect(newTile.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            cset.connect(newTile.getId(), ConstraintSet.END, gl.getId(), ConstraintSet.START);
            cset.applyTo(cl);
        } else {
            // in bestehende Reihe einfügen
            List<View> tableRows = LayoutHelper.getChildrenByClass(tl, TableRow.class);
            TableRow lastRow = (TableRow) tableRows.get(tableRows.size()-1);

            ConstraintLayout cl = (ConstraintLayout) LayoutHelper.getChildrenByClass(lastRow, ConstraintLayout.class).get(0);
            cl.addView(newTile);

            Guideline gl = (Guideline) LayoutHelper.getChildrenByClass(cl, Guideline.class).get(0);

            cset.connect(newTile.getId(), ConstraintSet.START, gl.getId(), ConstraintSet.START);
            cset.connect(newTile.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            cset.applyTo(cl);
        }
    }

    private ConstraintLayout addRow() {
        TableRow newTr = new TableRow(context);
        TableLayout.LayoutParams newTrParams = new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        newTr.setLayoutParams(newTrParams);

        ConstraintLayout newCl = new ConstraintLayout(context);
        TableLayout.LayoutParams newClParams = new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        newCl.setLayoutParams(newClParams);

        Guideline newGuideline = new Guideline(context);
        ConstraintLayout.LayoutParams newGuidelineParams = new ConstraintLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT
        ); // vertical Guideline
        newGuideline.setLayoutParams(newGuidelineParams);

        newCl.addView(newGuideline);
        newTr.addView(newCl);

        return newCl;
    }
}
