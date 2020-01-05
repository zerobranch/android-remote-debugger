/*
 * Copyright 2020 Arman Sargsyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
