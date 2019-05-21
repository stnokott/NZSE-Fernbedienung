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

    private int[] colorList = {
            Color.parseColor("#EF5350"),
            Color.parseColor("#AB47BC"),
            Color.parseColor("#FFA726"),
            Color.parseColor("#29B6F6"),
            Color.parseColor("#26A69A"),
            Color.parseColor("#D4E157"),
            Color.parseColor("#EC407A"),
            Color.parseColor("#66BB6A"),
            Color.parseColor("#5C6BC0"),
            Color.parseColor("#26C6DA")
    };
    private int nextIndex = 0;

    public ChannelTileManager(TableLayout tl) {
        this.context = tl.getContext();
        this.tl = tl;
    }

    public void addTile(String title) {
        // TODO: TileRow-Klasse schreiben, die Guideline- und Tile-Attribute hat, um unsichere Aufrufe zu getChildrenByClass zu vermeiden
        ChannelTile newTile = new ChannelTile(context, title, Integer.toString(nextIndex+1), colorList[nextIndex %colorList.length]);
        nextIndex++;

        if (tiles.size() % 2 == 0 || tiles.isEmpty()) {
            // neue Reihe anlegen
            ConstraintLayout cl = addRow();
            cl.addView(newTile);

            Guideline gl = (Guideline) LayoutHelper.getChildrenByClass(cl, Guideline.class).get(0);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) newTile.getLayoutParams();
            // links von Guideline
            params.startToStart = ConstraintSet.PARENT_ID;
            params.endToStart = gl.getId();
        } else {
            // in bestehende Reihe einfügen
            List<View> tableRows = LayoutHelper.getChildrenByClass(tl, TableRow.class);
            TableRow lastRow = (TableRow) tableRows.get(tableRows.size()-1);

            ConstraintLayout cl = (ConstraintLayout) LayoutHelper.getChildrenByClass(lastRow, ConstraintLayout.class).get(0);
            cl.addView(newTile);

            Guideline gl = (Guideline) LayoutHelper.getChildrenByClass(cl, Guideline.class).get(0);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) newTile.getLayoutParams();
            // rechts von Guideline
            params.startToStart = gl.getId();
            params.endToEnd = ConstraintSet.PARENT_ID;
        }
        tiles.add(newTile);
    }

    public ChannelTile getTileAt(int index) {
        return tiles.get(index);
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
        tl.addView(newTr);

        return newCl;
    }
}
