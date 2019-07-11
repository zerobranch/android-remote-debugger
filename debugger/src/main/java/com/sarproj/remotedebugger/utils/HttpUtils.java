package com.sarproj.remotedebugger.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.util.Locale;
import java.util.Objects;

import static android.content.Context.WIFI_SERVICE;

public final class HttpUtils {

    public static String getIpAccess(Context context) {
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        final int ipAddress = Objects.requireNonNull(wifiManager).getConnectionInfo().getIpAddress();
        return String.format(Locale.US, "%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }
}
