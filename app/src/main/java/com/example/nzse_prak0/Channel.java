package com.example.nzse_prak0;

public class Channel {
    private String channel;
    private String program;
    private String provider;

    public Channel(String channel, String program, String provider){
        this.setProgram(program);
        this.setChannel(channel);
        setProvider(provider);
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
}
