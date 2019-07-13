package com.sarproj.remotedebugger.source.managers.continuous;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sarproj.remotedebugger.RemoteDebugger;
import com.sarproj.remotedebugger.source.models.HttpLogModel;
import com.sarproj.remotedebugger.source.repository.HttpLogRepository;

public final class NetLogDataBaseManager {
    private static final String DATABASE_NAME = "remote_debugger_data.db";
    private final static Object LOCK = new Object();
    private final SQLiteDatabase database;
    private static NetLogDataBaseManager instance;
    private HttpLogRepository httpLogRepository;

    private NetLogDataBaseManager(Context context) {
        SQLiteDatabase.deleteDatabase(context.getDatabasePath(DATABASE_NAME));
        database = context.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
        database.setVersion(Integer.MAX_VALUE);

        httpLogRepository = new HttpLogRepository(database);
        httpLogRepository.createHttpLogsTable(database);
    }

    public static NetLogDataBaseManager getInstance() {
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
                instance = new NetLogDataBaseManager(context);
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

    public HttpLogModel addLog(HttpLogModel model) {
        synchronized (LOCK) {
            if (model.responseBody != null) {
                model.responseBody = model.responseBody.replaceAll("'", "''");
            }
            return httpLogRepository.add(model);
        }
    }

    public void update(HttpLogModel model) {
        synchronized (LOCK) {
            httpLogRepository.update(model);
        }
    }

    public void clearAll() {
        synchronized (LOCK) {
            httpLogRepository.clearAll();
        }
    }
}
