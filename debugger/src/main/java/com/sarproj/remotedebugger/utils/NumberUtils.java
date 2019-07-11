package com.sarproj.remotedebugger.utils;

public final class NumberUtils {

    public static boolean isInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
