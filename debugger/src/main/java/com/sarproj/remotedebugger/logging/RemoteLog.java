package com.sarproj.remotedebugger.logging;

import com.sarproj.remotedebugger.RemoteDebugger;
import com.sarproj.remotedebugger.source.managers.continuous.LogDataBaseManager;
import com.sarproj.remotedebugger.source.local.LogLevels;
import com.sarproj.remotedebugger.source.models.LogModel;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class RemoteLog {
    private static final String DEFAULT_TAG = RemoteLog.class.getSimpleName();

    public static void v(String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.VERBOSE.name(), DEFAULT_TAG, msg));
        }
    }

    public static void v(String tag, String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.VERBOSE.name(), tag, msg));
        }
    }

    public static void v(String tag, String msg, Throwable th) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.VERBOSE.name(), tag, msg + getStacktrace(th)));
        }
    }

    public static void d(String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.DEBUG.name(), DEFAULT_TAG, msg));
        }
    }

    public static void d(String tag, String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.DEBUG.name(), tag, msg));
        }
    }

    public static void d(String tag, String msg, Throwable th) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.DEBUG.name(), tag, msg + getStacktrace(th)));
        }
    }

    public static void i(String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.INFO.name(), DEFAULT_TAG, msg));
        }
    }

    public static void i(String tag, String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.INFO.name(), tag, msg));
        }
    }

    public static void i(String tag, String msg, Throwable th) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.INFO.name(), tag, msg + getStacktrace(th)));
        }
    }

    public static void w(String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.WARN.name(), DEFAULT_TAG, msg));
        }
    }

    public static void w(String tag, String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.WARN.name(), tag, msg));
        }
    }

    public static void w(String tag, String msg, Throwable th) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.WARN.name(), tag, msg + getStacktrace(th)));
        }
    }

    public static void e(String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.ERROR.name(), DEFAULT_TAG, msg));
        }
    }

    public static void e(String tag, String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.ERROR.name(), tag, msg));
        }
    }

    public static void e(String tag, String msg, Throwable th) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.ERROR.name(), tag, msg + getStacktrace(th)));
        }
    }

    public static void f(String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.FATAL.name(), DEFAULT_TAG, msg));
        }
    }

    public static void f(String tag, String msg) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.FATAL.name(), tag, msg));
        }
    }

    public static void f(String tag, String msg, Throwable th) {
        if (isAliveRemoteServices()) {
            getDataBase().add(new LogModel(LogLevels.FATAL.name(), tag, msg + getStacktrace(th)));
        }
    }

    private static String getStacktrace(Throwable th) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            th.printStackTrace(pw);
            return "\n".concat(sw.toString());
        } catch (IOException ignore) {
            return "";
        }
    }

    private static boolean isAliveRemoteServices() {
        return RemoteDebugger.isAlive();
    }

    private static LogDataBaseManager getDataBase() {
        return LogDataBaseManager.getInstance();
    }
}
