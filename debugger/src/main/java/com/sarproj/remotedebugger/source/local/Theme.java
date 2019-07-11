package com.sarproj.remotedebugger.source.local;

public enum Theme {
    DARK, LIGHT;

    public static boolean notContains(String inTheme) {
        for (Theme theme : values()) {
            if (theme.name().equalsIgnoreCase(inTheme)) {
                return true;
            }
        }
        return false;
    }
}
