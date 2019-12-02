package com.zerobranch.androidremotedebugger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    static final String ACTION_REPEAT_CONNECTION = "com.zerobranch.androidremotedebugger.REPEAT_CONNECTION";
    static final String ACTION_CHANGE_PORT = "com.zerobranch.androidremotedebugger.CHANGE_PORT";
    static final String ACTION_DISCONNECT = "com.zerobranch.androidremotedebugger.ACTION_DISCONNECT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) {
            return;
        }

        switch (action) {
            case ACTION_REPEAT_CONNECTION:
                AndroidRemoteDebugger.reconnect(context);
                break;
            case ACTION_CHANGE_PORT:
                AndroidRemoteDebugger.reconnectWithNewPort(context);
                break;
            case ACTION_DISCONNECT:
                AppNotification.cancelAll(context);
                AndroidRemoteDebugger.stop();
                break;
        }
    }
}
