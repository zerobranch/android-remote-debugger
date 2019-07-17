package com.sarproj.remotedebugger.logging;

import com.sarproj.remotedebugger.source.local.LogLevel;
import com.sarproj.remotedebugger.source.managers.ContinuousDataBaseManager;
import com.sarproj.remotedebugger.source.models.LogModel;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class RemoteLog {
    private static final String DEFAULT_TAG = RemoteLog.class.getSimpleName();
    private final boolean isEnabledDefaultLogging;
    private final ContinuousDataBaseManager continuousDataBaseManager;
    private final Logger logger;

    public RemoteLog(Logger logger, ContinuousDataBaseManager continuousDataBaseManager, boolean isEnabledDefaultLogging) {
        if (logger != null) {
            isEnabledDefaultLogging = true;
            this.logger = logger;
        } else {
            this.logger = new DefaultLogger();
        }

        this.isEnabledDefaultLogging = isEnabledDefaultLogging;
        this.continuousDataBaseManager = continuousDataBaseManager;
    }

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

        continuousDataBaseManager.addLog(new LogModel(logLevel.name(), tag, msg));

        if (isEnabledDefaultLogging) {
            logger.log(logLevel.priority(), tag, msg, th);
        }
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
}
