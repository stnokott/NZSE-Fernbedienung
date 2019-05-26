package com.example.nzse_prak0.helpers;

import android.os.Parcel;

public class Channel {
    private String channel;
    private String program;
    private String provider;
    private Boolean isFav = false;

    public Channel(String channel, String program, String provider){
        this(channel, program, provider, false);
    }

    public Channel(String channel, String program, String provider, Boolean isFav) {
        this.channel = channel;
        this.program = program;
        this.provider = provider;
        this.isFav = isFav;
    }

    private Channel(Parcel p) {
        this.channel = p.readString();
        this.program = p.readString();
        this.provider = p.readString();
        this.isFav = p.readInt() == 1;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Boolean getIsFav() { return isFav; }

    public void setIsFav(Boolean isFav) { this.isFav = isFav; }
}
