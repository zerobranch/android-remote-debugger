package com.sarproj.remotedebugger.source.managers.continuous;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sarproj.remotedebugger.RemoteDebugger;
import com.sarproj.remotedebugger.source.repository.LogRepository;
import com.sarproj.remotedebugger.source.models.LogModel;

import java.util.List;

public final class LogDataBaseManager {
    private static final String DATABASE_NAME = "remote_debugger_data.db";
    private static final Object LOCK = new Object();
    private final SQLiteDatabase database;
    private LogRepository logRepository;
    private static LogDataBaseManager instance;

    private LogDataBaseManager(Context context) {
        SQLiteDatabase.deleteDatabase(context.getDatabasePath(DATABASE_NAME));
        database = context.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
        database.setVersion(Integer.MAX_VALUE);

        logRepository = new LogRepository(database);
        logRepository.createLogsTable(database);
    }

    public static LogDataBaseManager getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException("RemoteDebugger is not initialized. " +
                        "Please call " + RemoteDebugger.class.getName() + ".init()");
            }
            return instance;
        }
    }

    public static void init(Context context) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new LogDataBaseManager(context);
            }
        }
    }

    public static void destroy() {
        synchronized (LOCK) {
            if (instance != null) {
                if (instance.database != null && instance.database.isOpen()) {
                    instance.database.close();
                }

                instance = null;
            }
        }
    }

    public void add(LogModel model) {
        synchronized (LOCK) {
            logRepository.addLog(model);
        }
    }

    public List<LogModel> getByFilter(int offset, String level, String tag, String search) {
        synchronized (LOCK) {
            return logRepository.getLogsByFilter(offset, level, tag, search);
        }
    }

    public void clearAll() {
        synchronized (LOCK) {
            logRepository.clearAllLogs();
        }
    }
}
