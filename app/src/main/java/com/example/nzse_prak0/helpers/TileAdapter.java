package com.example.nzse_prak0.helpers;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nzse_prak0.customviews.ChannelTile;

public class TileAdapter extends RecyclerView.Adapter<TileAdapter.TileViewHolder> {
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

    private View.OnClickListener onItemClickListener;
    private String[] channelNames;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class TileViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ChannelTile channelTile;
        public TileViewHolder(ChannelTile c) {
            super(c);
            channelTile = c;
        }

        public ChannelTile getChannelTile() {
            return channelTile;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TileAdapter(String[] myDataset) {
        channelNames = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public @NonNull TileViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        // create a new view
        ChannelTile c = new ChannelTile(parent.getContext(), "", "", Color.RED);

        return new TileViewHolder(c);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TileViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ChannelTile channelTile = holder.getChannelTile();
        channelTile.setTitle(channelNames[position]);
        channelTile.setBgNum(Integer.toString(position+1));
        channelTile.setColor(colorList[position%colorList.length]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return channelNames.length;
    }

    public void setOnItemClickListener(View.OnClickListener clickListener) {
        this.onItemClickListener = clickListener;
    }
}
