package com.sarproj.remotedebugger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    static final String ACTION_REPEAT_CONNECTION = "com.sarproj.remotedebugger.REPEAT_CONNECTION";
    static final String ACTION_DISABLE_OTHER = "com.sarproj.remotedebugger.DISABLE_OTHER";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) {
            return;
        }

        switch (action) {
            case ACTION_REPEAT_CONNECTION:
                RemoteDebugger.reconnect();
                break;
            case ACTION_DISABLE_OTHER:
                break;
        }
    }
}
