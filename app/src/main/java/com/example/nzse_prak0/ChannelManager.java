package com.example.nzse_prak0;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChannelManager {

    public String response;
    private ArrayList<Channel> channelList = new ArrayList<>();

    public void parseChannels(JSONObject json) throws JSONException {
        if (json.has("channels")) {              // Überprüfe ob "channels" vorhanden ist
            channelList.clear();
            JSONArray channels = json.getJSONArray("channels");
            response = json.getString("status");

            for (int i = 0; i < channels.length(); i++) {
                JSONObject channel = channels.getJSONObject(i);
                Channel newChannel = new Channel(channel.getString("channel"), channel.getString("program"), channel.getString("provider"));
                this.channelList.add(newChannel);
                Log.d(newChannel.getProgram(), "Program");
            }
        }
    }

    public Channel getChannelAt(int index) {
        return channelList.get(index);
    }

    public List<Channel> getChannels() {
        return channelList;
    }

    public List<Channel> getFavoriteChannels() {
        List<Channel> returnList = new ArrayList<>();
        for (Channel channel : channelList) {
            if (channel.getIsFav())
                returnList.add(channel);
        }
        return returnList;
    }
}

