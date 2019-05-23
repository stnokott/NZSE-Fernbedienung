package com.example.nzse_prak0;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ResolveJSON {

    public String response;

    protected ArrayList<String> programItems = new ArrayList<String>();
    protected ArrayList<String> channelItems = new ArrayList<String>();


    void parseChannels(JSONObject json) throws JSONException {

        if (json.has("channels")) {              // Überprüfe ob "channels" vorhanden ist

            JSONArray channels = json.getJSONArray("channels");
            response = json.getString("channels");

            for (int i = 0; i < channels.length(); i++) {
                JSONObject channel = channels.getJSONObject(i);
                Channel newChannel = new Channel(channel.getString("channel"), channel.getString("program"));
                programItems.add(newChannel.getProgram());
                channelItems.add(newChannel.getChannel());
                Log.d(newChannel.getProgram(), "Program");
            }
        }
    }
}

