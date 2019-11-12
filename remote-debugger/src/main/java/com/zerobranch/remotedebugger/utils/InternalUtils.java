package com.zerobranch.remotedebugger.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.WIFI_SERVICE;

public final class InternalUtils {

    public static String getIpAccess(Context context) {
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        final int ipAddress = Objects.requireNonNull(wifiManager).getConnectionInfo().getIpAddress();
        return String.format(Locale.US, "%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    public static String getStackTrace(Throwable th) {
        if (th == null) {
            return "";
        }

        final StringWriter sw = new StringWriter(256);
        final PrintWriter pw = new PrintWriter(sw, false);
        th.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static boolean isInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
