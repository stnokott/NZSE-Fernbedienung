package com.example.nzse_prak0.helpers;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nzse_prak0.customviews.ChannelTile;

import java.util.ArrayList;
import java.util.List;

public class TileAdapter extends RecyclerView.Adapter<TileAdapter.TileViewHolder> implements Filterable {
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
    private List<Channel> channelList;
    private List<Channel> channelListFiltered;
    private RecyclerView mRecyclerView;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    // nicht static
    public class TileViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ChannelTile channelTile;
        TileViewHolder(ChannelTile c) {
            super(c);
            channelTile = c;
            channelTile.setTag(this); // ViewHolder-Instanz als Tag, um von ChannelTile ViewHolder zu bekommen
            channelTile.setOnClickListener(onItemClickListener);
        }

        public ChannelTile getChannelTile() {
            return channelTile;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TileAdapter(List<Channel> channelList) {
        this.channelList = channelList;
        this.channelListFiltered = channelList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public @NonNull TileViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        // create a new view
        ChannelTile c = new ChannelTile(parent.getContext(), Color.RED);

        return new TileViewHolder(c);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TileViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ChannelTile channelTile = holder.getChannelTile();
        channelTile.setChannelInstance(channelListFiltered.get(position));
        channelTile.setColor(colorList[position%colorList.length]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return channelListFiltered.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public void setOnItemClickListener(View.OnClickListener clickListener) {
        this.onItemClickListener = clickListener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String s = charSequence.toString();
                if (s.isEmpty()) {
                    channelListFiltered = channelList;
                } else {
                    List<Channel> filteredList = new ArrayList<>();
                    for (Channel c : channelList) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (c.getProgram().toLowerCase().contains(s.toLowerCase())) {
                            filteredList.add(c);
                        }
                    }
                    channelListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = channelListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                channelListFiltered = (ArrayList<Channel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
        this.channelListFiltered = channelList;
        notifyDataSetChanged();
        mRecyclerView.scheduleLayoutAnimation();
    }
}
