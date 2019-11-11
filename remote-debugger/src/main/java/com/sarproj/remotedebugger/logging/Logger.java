package com.sarproj.remotedebugger.logging;

public interface Logger {
    void log(int priority, String tag, String msg, Throwable th);
}
