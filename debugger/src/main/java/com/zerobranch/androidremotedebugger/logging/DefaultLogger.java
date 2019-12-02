package com.zerobranch.androidremotedebugger.logging;

import android.util.Log;

public class DefaultLogger implements Logger {
    @Override
    public void log(int priority, String tag, String msg, Throwable th) {
        if (priority == Log.ASSERT) {
            Log.wtf(tag, msg);
        } else {
            Log.println(priority, tag, msg);
        }

        if (th != null) {
            th.printStackTrace();
        }
    }
}
