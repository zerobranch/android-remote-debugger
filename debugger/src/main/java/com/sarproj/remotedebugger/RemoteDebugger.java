package com.sarproj.remotedebugger;

import android.content.Context;

import com.sarproj.remotedebugger.logging.Logger;
import com.sarproj.remotedebugger.logging.RemoteLog;
import com.sarproj.remotedebugger.settings.InternalSettings;
import com.sarproj.remotedebugger.settings.SettingsPrefs;
import com.sarproj.remotedebugger.source.local.LogLevel;
import com.sarproj.remotedebugger.source.managers.ContinuousDBManager;

public final class RemoteDebugger {
    private static RemoteLog remoteLog;

    public synchronized static void init(final Builder builder) {
        if (isAlive()) {
            stop();
        }

        if (!builder.enabled) {
            return;
        }

        InternalSettings internalSettings = new InternalSettings(
                builder.enabledInternalLogging,
                builder.enabledJsonPrettyPrint
        );

        final Context context = builder.context.getApplicationContext();

        ServerRunner.getInstance().init(context, internalSettings, new ServerRunner.ConnectionStatus() {
            @Override
            public void onResult(boolean isRunning) {
                if (!isRunning) { // todo протестить эту шнягу, когда сервер занят другим приложением
                    return;
                }

                SettingsPrefs.init(context);
                ContinuousDBManager.init(context);
                remoteLog = new RemoteLog(builder.logger, ContinuousDBManager.getInstance(), builder.enabledDefaultLogging);
            }
        });
    }

    public synchronized static void init(Context context) {
        init(new Builder(context));
    }

    public synchronized static void stop() {
        ServerRunner.getInstance().stop();
        SettingsPrefs.destroy();
        ContinuousDBManager.destroy();
        remoteLog = null;
    }

    public static boolean isAlive() {
        return ServerRunner.getInstance().isAlive() || remoteLog != null;
    }

    public static class Builder {
        private final Context context;
        private boolean enabled = true;
        private boolean enabledDefaultLogging = false;
        private boolean enabledInternalLogging = false;
        private boolean enabledJsonPrettyPrint = false;
        private Logger logger;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder enableInternalLogging() {
            enabledInternalLogging = true;
            return this;
        }

        public Builder enableDefaultLogging() {
            enabledDefaultLogging = true;
            return this;
        }

        public Builder enableJsonPrettyPrint() {
            enabledJsonPrettyPrint = true;
            return this;
        }

        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }
    }

    public static class Log {
        public static void v(Throwable th) {
            log(LogLevel.VERBOSE, null, null, th);
        }

        public static void v(String msg) {
            log(LogLevel.VERBOSE, null, msg, null);
        }

        public static void v(String tag, String msg) {
            log(LogLevel.VERBOSE, tag, msg, null);
        }

        public static void v(String tag, String msg, Throwable th) {
            log(LogLevel.VERBOSE, tag, msg, th);
        }

        public static void d(Throwable th) {
            log(LogLevel.DEBUG, null, null, th);
        }

        public static void d(String msg) {
            log(LogLevel.DEBUG, null, msg, null);
        }

        public static void d(String tag, String msg) {
            log(LogLevel.DEBUG, tag, msg, null);
        }

        public static void d(String tag, String msg, Throwable th) {
            log(LogLevel.DEBUG, tag, msg, th);
        }

        public static void i(Throwable th) {
            log(LogLevel.INFO, null, null, th);
        }

        public static void i(String msg) {
            log(LogLevel.INFO, null, msg, null);
        }

        public static void i(String tag, String msg) {
            log(LogLevel.INFO, tag, msg, null);
        }

        public static void i(String tag, String msg, Throwable th) {
            log(LogLevel.INFO, tag, msg, th);
        }

        public static void w(Throwable th) {
            log(LogLevel.WARN, null, null, th);
        }

        public static void w(String msg) {
            log(LogLevel.WARN, null, msg, null);
        }

        public static void w(String tag, String msg) {
            log(LogLevel.WARN, tag, msg, null);
        }

        public static void w(String tag, String msg, Throwable th) {
            log(LogLevel.WARN, tag, msg, th);
        }

        public static void e(Throwable th) {
            log(LogLevel.ERROR, null, null, th);
        }

        public static void e(String msg) {
            log(LogLevel.ERROR, null, msg, null);
        }

        public static void e(String tag, String msg) {
            log(LogLevel.ERROR, tag, msg, null);
        }

        public static void e(String tag, String msg, Throwable th) {
            log(LogLevel.ERROR, tag, msg, th);
        }

        public static void f(Throwable th) {
            log(LogLevel.FATAL, null, null, th);
        }

        public static void f(String msg) {
            log(LogLevel.FATAL, null, msg, null);
        }

        public static void f(String tag, String msg) {
            log(LogLevel.FATAL, tag, msg, null);
        }

        public static void f(String tag, String msg, Throwable th) {
            log(LogLevel.FATAL, tag, msg, th);
        }

        public static void log(int priority, String tag, String msg, Throwable th) {
            LogLevel logLevel = LogLevel.getByPriority(priority);

            if (logLevel != null) {
                log(logLevel, tag, msg, th);
            }
        }

        private static void log(LogLevel logLevel, String tag, String msg, Throwable th) {
            // TODO: при (remoteLog == null) сделать exception
            // TODO: протестировать при каких условиях isAlive == false, возможно тогда, когда запущен другой сервер и сделать свой exception

            if (!isAlive()) {
                return;
            }

            remoteLog.log(logLevel, tag, msg, th);
        }
    }
}
