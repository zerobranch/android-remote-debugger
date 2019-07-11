package com.sarproj.remotedebugger.source.models;

public class LogModel {
    public long time;
    public String level;
    public String tag;
    public String message;

    public LogModel() {
    }

    public LogModel(String level, String tag, String message) {
        this.level = level;
        this.tag = tag;
        this.message = message;
    }

    public long getNewTime() {
        time = System.currentTimeMillis();
        return time;
    }
}
