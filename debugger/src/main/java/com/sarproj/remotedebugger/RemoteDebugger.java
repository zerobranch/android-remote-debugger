package com.sarproj.remotedebugger;

import android.content.Context;

import com.sarproj.remotedebugger.source.managers.continuous.LogDataBaseManager;
import com.sarproj.remotedebugger.settings.Settings;
import com.sarproj.remotedebugger.source.managers.continuous.NetLogDataBaseManager;

public final class RemoteDebugger {

    private RemoteDebugger() {
        throw new AssertionError("No '" + RemoteDebugger.class.getName() + "' instances!");
    }

    public static void init(Context context) {
        init(context, true, false);
    }

    public static void init(Context context, boolean enable) {
        init(context, enable, false);
    }

    public synchronized static void init(Context context, boolean enable, boolean isEnableLogging) {
        if (!enable)
            return;

        Settings.init(context.getApplicationContext());
        LogDataBaseManager.init(context.getApplicationContext());
        NetLogDataBaseManager.init(context.getApplicationContext());
        ServerRunner.getInstance().init(context.getApplicationContext(), isEnableLogging);
    }

    public synchronized static void stop() {
        ServerRunner.getInstance().stop();
        Settings.destroy();
        LogDataBaseManager.destroy();
        NetLogDataBaseManager.destroy();
    }

    public static boolean isAlive() {
        return ServerRunner.getInstance().isAlive();
    }
}

