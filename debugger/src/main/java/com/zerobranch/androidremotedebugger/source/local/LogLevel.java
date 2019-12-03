package com.zerobranch.androidremotedebugger.source.local;

public enum LogLevel {
    VERBOSE(2),
    DEBUG(3),
    INFO(4),
    WARN(5),
    ERROR(6),
    FATAL(7);

    private final int priority;

    LogLevel(int priority) {
        this.priority = priority;
    }

    public int priority() {
        return priority;
    }

    public static LogLevel getByPriority(int priority) {
        for (LogLevel logLevel : LogLevel.values()) {
            if (logLevel.priority == priority) {
                return logLevel;
            }
        }

        return null;
    }
}