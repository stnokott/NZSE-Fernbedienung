package com.example.nzse_prak0;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChannelManager {

    public String response;
    protected ArrayList<Channel> channelList = new ArrayList<>();

    public void parseChannels(JSONObject json) throws JSONException {

        if (json.has("channels")) {              // Überprüfe ob "channels" vorhanden ist

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
}
