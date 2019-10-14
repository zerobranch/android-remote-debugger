package com.sarproj.remotedebugger.source.models;

import com.sarproj.remotedebugger.source.local.Constants;

public class LogModel {
    public String time;
    public String level;
    public String tag;
    public String message;

    public LogModel() {
    }

    public LogModel(String level, String tag, String message, long currentTime) {
        this.level = level;
        this.tag = tag;
        this.message = message;
        this.time = Constants.defaultDateFormat.format(currentTime);
    }
}
