package com.sarproj.remotedebugger.settings;

public class InternalSettings {
    private boolean enabledInternalLogging;
    private boolean enabledJsonPrettyPrint;

    public InternalSettings(boolean enabledInternalLogging, boolean enabledJsonPrettyPrint) {
        this.enabledInternalLogging = enabledInternalLogging;
        this.enabledJsonPrettyPrint = enabledJsonPrettyPrint;
    }

    public boolean isEnabledInternalLogging() {
        return enabledInternalLogging;
    }

    public boolean isEnabledJsonPrettyPrint() {
        return enabledJsonPrettyPrint;
    }
}
