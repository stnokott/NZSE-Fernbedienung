package com.example.nzse_prak0.helpers;

public class Channel {
    private String channel;
    private String program;
    private String provider;
    private Boolean isFav;

    public Channel(String channel, String program, String provider){
        this(channel, program, provider, false);
    }

    public Channel(String channel, String program, String provider, Boolean isFav) {
        this.setProgram(program);
        this.setChannel(channel);
        this.provider = provider;
        this.isFav = isFav;
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
