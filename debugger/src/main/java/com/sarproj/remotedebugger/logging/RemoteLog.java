package com.sarproj.remotedebugger.logging;

import android.os.Build;

import com.sarproj.remotedebugger.RemoteDebugger;
import com.sarproj.remotedebugger.source.local.LogLevel;
import com.sarproj.remotedebugger.source.managers.ContinuousDBManager;
import com.sarproj.remotedebugger.source.models.LogModel;
import com.sarproj.remotedebugger.utils.InternalUtils;

import org.jetbrains.annotations.NotNull;

public final class RemoteLog {
    private static final String DEFAULT_TAG = RemoteLog.class.getSimpleName();
    private final boolean isEnabledDuplicateLogging;
    private ContinuousDBManager continuousDBManager;
    private final Logger logger;
    private static final int MAX_LOG_LENGTH = 2000;
    private static final int MAX_TAG_LENGTH = 23;

    public RemoteLog(Logger logger, boolean isEnabledDuplicateLogging) {
        if (logger != null) {
            isEnabledDuplicateLogging = true;
            this.logger = logger;
        } else {
            // todo тут наверное что то странное (если активирован isEnabledDefaultLogging и указан logger то наверное долно чтобы и логер работал и логер по умолчанию)
            // не знаю, но лучше сделать как то по лакониченей, чтобы была однозначность
            // DefaultLogger сделать типа как в timber DEFAULT
            this.logger = new DefaultLogger();
        }

        this.isEnabledDuplicateLogging = isEnabledDuplicateLogging;

        if (RemoteDebugger.isAliveWebServer()) {
            continuousDBManager = ContinuousDBManager.getInstance();
        }
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

        if (continuousDBManager != null) {
            continuousDBManager.addLog(new LogModel(logLevel.name(), tag, msg));
        }

        if (isEnabledDuplicateLogging) {
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
