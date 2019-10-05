package com.sarproj.remotedebugger;

import android.content.Context;
import android.util.Log;

import com.sarproj.remotedebugger.settings.InternalSettings;
import com.sarproj.remotedebugger.utils.InternalUtils;

import fi.iki.elonen.NanoHTTPD;

final class ServerRunner {
    private static final String TAG = "RemoteDebugger";
    private static final int DEFAULT_PORT = 8080;
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

    void init(Context context, InternalSettings internalSettings, ConnectionStatus connectionStatus) {
        if (isAlive()) {
            print("Server is already running");
            connectionStatus.onResult(true);
            return;
        }

        this.enabledInternalLogging = internalSettings.isEnabledInternalLogging();

        String ip = InternalUtils.getIpAccess(context);

        try {
            androidWebServer = new AndroidWebServer(context, ip, DEFAULT_PORT, internalSettings);
            androidWebServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);

            print(String.format("Android Remote Debugger is started. Web server ip: %s", ip));
            connectionStatus.onResult(true);
        } catch (Exception ex) {
            printErr("Could not start server", ex);
            connectionStatus.onResult(false);
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
        void onResult(boolean isRunning);
    }
}
