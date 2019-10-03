package com.sarproj.remotedebugger.logging;

import com.sarproj.remotedebugger.source.local.LogLevel;
import com.sarproj.remotedebugger.source.managers.ContinuousDataBaseManager;
import com.sarproj.remotedebugger.source.models.LogModel;
import com.sarproj.remotedebugger.utils.InternalUtils;

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
            // todo тут наверное что то странное (если активирован isEnabledDefaultLogging и указан logger то наверное долно чтобы и логер работал и логер по умолчанию)
            // не знаю, но лучше сделать как то по лакониченей, чтобы была однозначность
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
            msg = InternalUtils.getStackTrace(th);
        } else {
            if (th != null) {
                msg += "\n" + InternalUtils.getStackTrace(th);
            }
        }

        continuousDataBaseManager.addLog(new LogModel(logLevel.name(), tag, msg));

        if (isEnabledDefaultLogging) {
            logger.log(logLevel.priority(), tag, msg, th);
        }
    }
}
