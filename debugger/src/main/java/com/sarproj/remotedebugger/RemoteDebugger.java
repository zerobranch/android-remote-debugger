package com.sarproj.remotedebugger;

import android.content.Context;

import com.sarproj.remotedebugger.logging.DefaultLogger;
import com.sarproj.remotedebugger.logging.Logger;
import com.sarproj.remotedebugger.logging.RemoteLog;
import com.sarproj.remotedebugger.settings.InternalSettings;
import com.sarproj.remotedebugger.settings.SettingsPrefs;
import com.sarproj.remotedebugger.source.local.LogLevel;
import com.sarproj.remotedebugger.source.managers.ContinuousDBManager;

public final class RemoteDebugger {
    private static RemoteLog remoteLog;
    private Builder builder;

    private RemoteDebugger(Builder builder) {
        this.builder = builder;
    }

    public synchronized static void init(Context context) {
        init(context, new Builder().build());
    }

    public synchronized static void init(final Context context, final RemoteDebugger remoteDebugger) {
        if (isAliveWebServer()) {
            stop();
        }

        if (!remoteDebugger.builder.enabled) {
            return;
        }

        if (remoteDebugger.builder.includedUncaughtException) {
            setUncaughtExceptionHandler();
        }

        InternalSettings internalSettings = new InternalSettings(
                remoteDebugger.builder.enabledInternalLogging,
                remoteDebugger.builder.enabledJsonPrettyPrint
        );

        ServerRunner.getInstance().init(context, internalSettings, new ServerRunner.ConnectionStatus() {
            @Override
            public void onResult(boolean isSuccessRunning) {
                if (isSuccessRunning) {
                    Context appContext = context.getApplicationContext();

                    SettingsPrefs.init(appContext);
                    ContinuousDBManager.init(appContext);
                }

                remoteLog = new RemoteLog(remoteDebugger.builder.logger);
            }
        });
    }

    private static void setUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            private Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.f(e);
                originalHandler.uncaughtException(t, e);
            }
        });
    }

    public synchronized static void stop() {
        ServerRunner.getInstance().stop();
        SettingsPrefs.destroy();
        ContinuousDBManager.destroy();
        remoteLog = null;
    }

    public static boolean isAliveWebServer() {
        return ServerRunner.getInstance().isAlive();
    }

    public static class Builder {
        private boolean enabled = true;
        private boolean enabledInternalLogging = false;
        private boolean enabledJsonPrettyPrint = false;
        private boolean includedUncaughtException = true;
        private Logger logger;

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder enableInternalLogging() {
            enabledInternalLogging = true;
            return this;
        }

        public Builder enableDuplicateLogging() {
            this.logger = new DefaultLogger();
            return this;
        }

        public Builder enableDuplicateLogging(Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder enableJsonPrettyPrint() {
            enabledJsonPrettyPrint = true;
            return this;
        }

        public Builder excludeUncaughtException() {
            includedUncaughtException = false;
            return this;
        }

        public RemoteDebugger build() {
            return new RemoteDebugger(this);
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
            if (remoteLog != null) {
                remoteLog.log(logLevel, tag, msg, th);
            }
        }
    }
}
