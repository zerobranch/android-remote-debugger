package com.zerobranch.remotedebugger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    static final String ACTION_REPEAT_CONNECTION = "com.zerobranch.remotedebugger.REPEAT_CONNECTION";
    static final String ACTION_CHANGE_PORT = "com.zerobranch.remotedebugger.CHANGE_PORT";
    static final String ACTION_DISCONNECT = "com.zerobranch.remotedebugger.ACTION_DISCONNECT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) {
            return;
        }

        switch (action) {
            case ACTION_REPEAT_CONNECTION:
                RemoteDebugger.reconnect(context);
                break;
            case ACTION_CHANGE_PORT:
                RemoteDebugger.reconnectWithNewPort(context);
                break;
            case ACTION_DISCONNECT:
                AppNotification.cancelAll(context);
                RemoteDebugger.stop();
                break;
        }
    }
}
