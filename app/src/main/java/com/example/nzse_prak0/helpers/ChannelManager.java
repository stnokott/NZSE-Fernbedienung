package com.example.nzse_prak0.helpers;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChannelManager {
    private static final String JSON_FILENAME_CHANNELS = "channels.json";

    private String response;
    private ArrayList<Channel> channelList = new ArrayList<>();

    private static final String JSON_KEY_CHANNEL = "channel";
    private static final String JSON_KEY_PROGRAM = "program";
    private static final String JSON_KEY_PROVIDER = "provider";
    private static final String JSON_KEY_FAVORITE = "isFav";

    public void parseJSON(JSONObject json) throws JSONException {
        if (json.has("channels")) {              // Überprüfe ob "channels" vorhanden ist
            List<String> occupiedPrograms = new ArrayList<>();

            channelList.clear();
            JSONArray channels = json.getJSONArray("channels");
            response = json.getString("status");

            for (int i = 0; i < channels.length(); i++) {
                JSONObject channel = channels.getJSONObject(i);
                String program = channel.getString(JSON_KEY_PROGRAM);
                if (!occupiedPrograms.contains(program)) {
                    Channel newChannel = new Channel(channel.getString(JSON_KEY_CHANNEL), program, channel.getString(JSON_KEY_PROVIDER));
                    this.channelList.add(newChannel);
                    Log.d(newChannel.getProgram(), "Program");
                    occupiedPrograms.add(program);
                } else {
                    Log.d(program, "übersprungen, bereits vorhanden");
                }
            }
            sort();
        }
    }

    public void saveToJSON(Context context) {
        try (OutputStream output = context.getApplicationContext().openFileOutput(JSON_FILENAME_CHANNELS, Context.MODE_PRIVATE)) {
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for (Channel c : channelList) {
                writer.beginObject();
                writer.name(JSON_KEY_CHANNEL).value(c.getChannel());
                writer.name(JSON_KEY_PROGRAM).value(c.getProgram());
                writer.name(JSON_KEY_PROVIDER).value(c.getProvider());
                writer.name(JSON_KEY_FAVORITE).value(c.getIsFav());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
            //Toast.makeText(context.getApplicationContext(), "JSON gespeichert!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("saveToJSON", e.getMessage());
        }
    }

    public void loadFromJSON(Context context) {
        try (InputStream input = context.getApplicationContext().openFileInput(JSON_FILENAME_CHANNELS)) {
            JsonReader reader = new JsonReader(new InputStreamReader(input, StandardCharsets.UTF_8));

            channelList.clear();
            reader.beginArray();
            while(reader.hasNext()) {
                String channel = null;
                String program = null;
                String provider = null;
                Boolean isFav = null;

                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    switch(name) {
                        case JSON_KEY_CHANNEL:
                            channel = reader.nextString();
                            break;
                        case JSON_KEY_PROGRAM:
                            program = reader.nextString();
                            break;
                        case JSON_KEY_PROVIDER:
                            provider = reader.nextString();
                            break;
                        case JSON_KEY_FAVORITE:
                            isFav = reader.nextBoolean();
                            break;
                        default:
                            throw(new IllegalArgumentException("JSON inkorrekt formatiert"));
                    }
                }
                reader.endObject();
                channelList.add(new Channel(channel, program, provider, isFav));
            }
            reader.endArray();
            sort();
        } catch (IOException e) {
            Log.e("saveToJSON", e.getMessage());
        }
    }

    // TODO: stattdessen SortedList verwenden https://stackoverflow.com/a/30429439
    private void sort() {
        Collections.sort(channelList, new Comparator<Channel>() {
            @Override
            public int compare(Channel o1, Channel o2) {
                return o1.getProgram().compareTo(o2.getProgram());
            }
        });
    }

    public List<Channel> getChannels() {
        return channelList;
    }

    public Channel getChannelAt(int index) {
        return channelList.get(index);
    }

    public List<Channel> getFavoriteChannels() {
        List<Channel> returnList = new ArrayList<>();
        for (Channel channel : channelList) {
            if (channel.getIsFav())
                returnList.add(channel);
        }
        return returnList;
    }

    public int getChannelCount() {
        return channelList.size();
    }

    public String getResponse() {
        return response;
    }
}

