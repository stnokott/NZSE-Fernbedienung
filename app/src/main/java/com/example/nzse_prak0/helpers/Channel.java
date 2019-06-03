package com.example.nzse_prak0.helpers;

public class Channel {
    private String channelId;
    private String program;
    private String provider;
    private Boolean isFav;

    public Channel(String channelId, String program, String provider) {
        this(channelId, program, provider, false);
    }

    public Channel(String channelId, String program, String provider, Boolean isFav) {
        this.channelId = channelId;
        this.program = program;
        this.provider = provider;
        this.isFav = isFav;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
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
