package com.zerobranch.remotedebugger.logging;

import android.os.Build;

import com.zerobranch.remotedebugger.source.local.LogLevel;
import com.zerobranch.remotedebugger.source.managers.ContinuousDBManager;
import com.zerobranch.remotedebugger.source.models.LogModel;
import com.zerobranch.remotedebugger.utils.InternalUtils;

import org.jetbrains.annotations.NotNull;

public final class RemoteLog {
    private static final String DEFAULT_TAG = RemoteLog.class.getSimpleName();
    private static final int MAX_LOG_LENGTH = 2000;
    private static final int MAX_TAG_LENGTH = 23;
    private final ContinuousDBManager continuousDBManager;
    private final Logger logger;

    public RemoteLog(Logger logger) {
        this.logger = logger;
        continuousDBManager = ContinuousDBManager.getInstance();
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

        continuousDBManager.addLog(new LogModel(logLevel.name(), tag, msg, System.currentTimeMillis()));

        if (logger != null) {
            if (logger instanceof DefaultLogger) {
                if (tag.length() > MAX_TAG_LENGTH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    tag = tag.substring(0, MAX_TAG_LENGTH);
                }

                partialLogs(logLevel.priority(), tag, msg, th);
            } else {
                logger.log(logLevel.priority(), tag, msg, th);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void partialLogs(int priority, String tag, @NotNull String msg, Throwable th) {
        if (msg.length() < MAX_LOG_LENGTH) {
            logger.log(priority, tag, msg, th);
            return;
        }

        for (int i = 0, length = msg.length(); i < length; i++) {
            int newline = msg.indexOf('\n', i);
            newline = newline != -1 ? newline : length;
            do {
                int end = Math.min(newline, i + MAX_LOG_LENGTH);
                String part = msg.substring(i, end);
                logger.log(priority, tag, part, th);
                i = end;
            } while (i < newline);
        }
    }
}
