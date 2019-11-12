package com.zerobranch.remotedebugger;

import android.content.Context;
import android.util.Log;

import com.zerobranch.remotedebugger.settings.InternalSettings;
import com.zerobranch.remotedebugger.utils.InternalUtils;

import fi.iki.elonen.NanoHTTPD;

final class ServerRunner {
    private static final String TAG = "RemoteDebugger";
    private static volatile ServerRunner instance;
    private AndroidWebServer androidWebServer;
    private boolean enabledInternalLogging;

    private ServerRunner() { }

    static ServerRunner getInstance() {
        ServerRunner localInstance = instance;
        if (localInstance == null) {
            synchronized (ServerRunner.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ServerRunner();
                }
            }
        }
        return localInstance;
    }

    void init(Context context, InternalSettings internalSettings, int port, ConnectionStatus connectionStatus) {
        String ip = InternalUtils.getIpAccess(context);
        String ipPort = ip + ":" + port;

        if (isAlive()) {
            print("Server is already running");
            connectionStatus.onResult(true, ipPort);
            return;
        }

        this.enabledInternalLogging = internalSettings.isEnabledInternalLogging();

        try {
            androidWebServer = new AndroidWebServer(context, ip, port, internalSettings);
            androidWebServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);

            print(String.format("Android Remote Debugger is started. Go to: http://%s", ipPort));
            connectionStatus.onResult(true, ipPort);
        } catch (Exception ex) {
            printErr(String.format("Failed connection. %s is busy", ipPort), ex);
            connectionStatus.onResult(false, ipPort);
        }
    }

    boolean isAlive() {
        return androidWebServer != null && androidWebServer.isAlive();
    }

    void stop() {
        if (androidWebServer != null && androidWebServer.isAlive()) {
            androidWebServer.stop();
            print("Android Remote Debugger is stopped.");
        }

        androidWebServer = null;
        instance = null;
    }

    private void print(String text) {
        if (enabledInternalLogging)
            Log.d(TAG, text);
    }

    private void printErr(String text, Throwable th) {
        if (enabledInternalLogging)
            Log.e(TAG, text, th);
    }

    interface ConnectionStatus {
        void onResult(boolean isSuccessRunning, String ipPort);
    }
}
