package com.example.nzse_prak0.helpers;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.Toast;

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
import java.util.List;

public class ChannelManager {
    private static final String JSON_FILENAME_CHANNELS = "channels.json";

    private String response;
    private ArrayList<Channel> channelList = new ArrayList<>();

    public void parseJSON(JSONObject json) throws JSONException {
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

    public void saveToJSON(Context context) {
        try (OutputStream output = context.getApplicationContext().openFileOutput(JSON_FILENAME_CHANNELS, Context.MODE_PRIVATE)) {
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for (Channel c : channelList) {
                writer.beginObject();
                writer.name("channel").value(c.getChannel());
                writer.name("program").value(c.getProgram());
                writer.name("provider").value(c.getProvider());
                writer.name("isFav").value(c.getIsFav());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
            Toast.makeText(context.getApplicationContext(), "JSON gespeichert!", Toast.LENGTH_LONG).show();
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
                        case "channel":
                            channel = reader.nextString();
                            break;
                        case "program":
                            program = reader.nextString();
                            break;
                        case "provider":
                            provider = reader.nextString();
                            break;
                        case "isFav":
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
        } catch (IOException e) {
            Log.e("saveToJSON", e.getMessage());
        }
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

    public String getResponse() {
        return response;
    }
}

