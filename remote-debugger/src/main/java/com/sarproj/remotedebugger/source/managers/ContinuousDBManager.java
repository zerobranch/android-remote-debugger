package com.sarproj.remotedebugger.source.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;

import com.sarproj.remotedebugger.RemoteDebugger;
import com.sarproj.remotedebugger.source.local.StatusCodeFilter;
import com.sarproj.remotedebugger.source.models.LogModel;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogModel;
import com.sarproj.remotedebugger.source.repository.HttpLogRepository;
import com.sarproj.remotedebugger.source.repository.LogRepository;

import java.util.List;

public final class ContinuousDBManager {
    private static final String DATABASE_NAME = "remote_debugger_data.db";
    private final static Object LOCK = new Object();
    private final SQLiteDatabase database;
    private static ContinuousDBManager instance;
    private final HttpLogRepository httpLogRepository;
    private final LogRepository logRepository;
    private final Handler loggingHandler;
    private final HandlerThread loggingHandlerThread;

    private ContinuousDBManager(Context context) {
        loggingHandlerThread = new HandlerThread("LoggingHandlerThread");
        loggingHandlerThread.start();
        loggingHandler = new Handler(loggingHandlerThread.getLooper());

        SQLiteDatabase.deleteDatabase(context.getDatabasePath(DATABASE_NAME));
        database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        database.setVersion(Integer.MAX_VALUE);

        httpLogRepository = new HttpLogRepository(database);
        httpLogRepository.createHttpLogsTable(database);

        logRepository = new LogRepository(database);
        logRepository.createLogsTable(database);
    }

    public static ContinuousDBManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("RemoteDebugger is not initialized. " +
                    "Please call " + RemoteDebugger.class.getName() + ".init()");
        }
        return instance;
    }

    public static void init(Context context) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new ContinuousDBManager(context);
            }
        }
    }

    public static void destroy() {
        if (instance != null) {
            instance.loggingHandlerThread.quit();

            if (instance.database != null && instance.database.isOpen()) {
                instance.database.close();
            }

            instance = null;
        }
    }

    public long addHttpLog(HttpLogModel logModel) {
        synchronized (LOCK) {
            return httpLogRepository.add(logModel);
        }
    }

    public void clearAllHttpLogs() {
        synchronized (LOCK) {
            loggingHandler.post(new Runnable() {
                @Override
                public void run() {
                    httpLogRepository.clearAll();
                }
            });
        }
    }

    public void addLog(final LogModel model) {
        synchronized (LOCK) {
            loggingHandler.post(new Runnable() {
                @Override
                public void run() {
                    logRepository.addLog(model);
                }
            });
        }
    }

    public List<LogModel> getLogsByFilter(int offset, int limit, String level, String tag, String search) {
        synchronized (LOCK) {
            return logRepository.getLogsByFilter(offset, limit, level, tag, search);
        }
    }

    public void clearAllLogs() {
        synchronized (LOCK) {
            loggingHandler.post(new Runnable() {
                @Override
                public void run() {
                    logRepository.clearAllLogs();
                }
            });
        }
    }

    public List<HttpLogModel> getHttpLogs(int offset,
                                          int limit,
                                          StatusCodeFilter statusCode,
                                          boolean isOnlyErrors,
                                          String search) {
        synchronized (LOCK) {
            return httpLogRepository.getHttpLogs(offset, limit, statusCode, isOnlyErrors, search);
        }
    }
}
