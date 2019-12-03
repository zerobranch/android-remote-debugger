package com.zerobranch.androidremotedebugger.settings;

public class InternalSettings {
    private final boolean enabledInternalLogging;
    private final boolean enabledJsonPrettyPrint;

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
