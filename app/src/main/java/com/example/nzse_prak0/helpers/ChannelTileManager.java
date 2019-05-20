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

import java.util.ArrayList;
import java.util.List;

public class ChannelTileManager {
    private Context context;
    private TableLayout tl;
    private List<ChannelTile> tiles = new ArrayList<>();

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

            // links von Guideline
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

            // rechts von Guideline
            cset.connect(newTile.getId(), ConstraintSet.START, gl.getId(), ConstraintSet.START);
            cset.connect(newTile.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            cset.applyTo(cl);
        }
        tiles.add(newTile);
    }

    private ConstraintLayout addRow() {
        TableRow newTr = new TableRow(context);
        TableRow.LayoutParams newTrParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
        );
        newTr.setLayoutParams(newTrParams);

        ConstraintLayout newCl = new ConstraintLayout(context);
        TableRow.LayoutParams newClParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        newCl.setLayoutParams(newClParams);

        Guideline newGuideline = new Guideline(context);
        newGuideline.setId(View.generateViewId());
        ConstraintLayout.LayoutParams newGuidelineParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ); // vertical Guideline
        newGuidelineParams.orientation = ConstraintLayout.LayoutParams.VERTICAL;
        newGuidelineParams.guidePercent = 0.5f;
        newGuideline.setLayoutParams(newGuidelineParams);

        newCl.addView(newGuideline);
        newTr.addView(newCl);
        tl.addView(newTr, 0); // TODO: index-Attribut entfernen

        return newCl;
    }
}
