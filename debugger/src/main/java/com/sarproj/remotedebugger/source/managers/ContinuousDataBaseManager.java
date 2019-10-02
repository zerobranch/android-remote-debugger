package com.sarproj.remotedebugger.source.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sarproj.remotedebugger.RemoteDebugger;
import com.sarproj.remotedebugger.source.local.StatusCodeFilter;
import com.sarproj.remotedebugger.source.models.LogModel;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogModel;
import com.sarproj.remotedebugger.source.repository.HttpLogRepository;
import com.sarproj.remotedebugger.source.repository.LogRepository;

import java.util.List;

public final class ContinuousDataBaseManager {
    private static final String DATABASE_NAME = "remote_debugger_data.db";
    private final static Object LOCK = new Object();
    private final SQLiteDatabase database;
    private static ContinuousDataBaseManager instance;
    private final HttpLogRepository httpLogRepository;
    private final LogRepository logRepository;

    private ContinuousDataBaseManager(Context context) {
        SQLiteDatabase.deleteDatabase(context.getDatabasePath(DATABASE_NAME));
        database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        database.setVersion(Integer.MAX_VALUE);

        httpLogRepository = new HttpLogRepository(database);
        httpLogRepository.createHttpLogsTable(database);

        logRepository = new LogRepository(database);
        logRepository.createLogsTable(database);
    }

    public static ContinuousDataBaseManager getInstance() {
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
                instance = new ContinuousDataBaseManager(context);
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

    public long addHttpLog(HttpLogModel logModel) {
        synchronized (LOCK) {
            return httpLogRepository.add(logModel);
        }
    }

    public void clearAllHttpLog() {
        synchronized (LOCK) {
            httpLogRepository.clearAll();
        }
    }

    public void addLog(LogModel model) {
        synchronized (LOCK) {
            logRepository.addLog(model);
        }
    }

    public List<LogModel> getLogByFilter(int offset, int limit, String level, String tag, String search) {
        synchronized (LOCK) {
            return logRepository.getLogsByFilter(offset, limit, level, tag, search);
        }
    }

    public void clearAllLog() {
        synchronized (LOCK) {
            logRepository.clearAllLogs();
        }
    }

    public List<HttpLogModel> getHttpLogs(int offset,
                                          int limit,
                                          StatusCodeFilter statusCode,
                                          boolean isOnlyErrors,
                                          String search) {
        return httpLogRepository.getHttpLogs(offset, limit, statusCode, isOnlyErrors, search);
    }
}
