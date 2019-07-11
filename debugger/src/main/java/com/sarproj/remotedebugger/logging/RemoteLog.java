package com.sarproj.remotedebugger.logging;

import com.sarproj.remotedebugger.RemoteDebugger;
import com.sarproj.remotedebugger.source.local.LogLevel;
import com.sarproj.remotedebugger.source.managers.continuous.LogDataBaseManager;
import com.sarproj.remotedebugger.source.models.LogModel;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class RemoteLog {
    private static final String DEFAULT_TAG = RemoteLog.class.getSimpleName();

    public void log(LogLevel logLevel, String tag, String msg, Throwable th) {
        if (tag == null) {
            tag = DEFAULT_TAG;
        }

        if (msg != null && msg.length() == 0) {
            msg = null;
        }

        if (msg == null) {
            if (th == null) {
                return;
            }
            msg = getStacktrace(th);
        } else {
            if (th != null) {
                msg += "\n" + getStacktrace(th);
            }
        }

        getDataBase().add(new LogModel(logLevel.name(), tag, msg));

        if (RemoteDebugger.isEnabledDefaultLogging()) {
            defaultLog(logLevel.priority(), tag, msg, th);
        }
    }

    private void defaultLog(int priority, String tag, String msg, Throwable th) {
        Logger logger = RemoteDebugger.getLogger();
        if (logger == null) {
            logger = new DefaultLogger();
        }
        logger.log(priority, tag, msg, th);
    }

    private String getStacktrace(Throwable th) {
        if (th == null) {
            return "";
        }

        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        th.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private LogDataBaseManager getDataBase() {
        return LogDataBaseManager.getInstance();
    }
}
