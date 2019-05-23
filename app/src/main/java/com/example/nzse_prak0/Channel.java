package com.example.nzse_prak0;

public class Channel {
    private long frequency;
    private String channel;
    private int quality;
    private String program;
    private String provider;

    public Channel(String channel, String program){
        this.setProgram(program);
        this.setChannel(channel);
        setQuality(0);
        setProvider("");
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
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
}
