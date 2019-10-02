package com.sarproj.remotedebugger.source.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sarproj.remotedebugger.RemoteDebugger;
import com.sarproj.remotedebugger.source.local.StatusCodeFilter;
import com.sarproj.remotedebugger.source.models.LogModel;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogModel;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogRequest;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogResponse;
import com.sarproj.remotedebugger.source.models.httplog.QueryType;
import com.sarproj.remotedebugger.source.repository.HttpLogRepository;
import com.sarproj.remotedebugger.source.repository.LogRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ContinuousDataBaseManager {
    private static final String DATABASE_NAME = "remote_debugger_data.db";
    private final static Object LOCK = new Object();
    private final SQLiteDatabase database;
    private static ContinuousDataBaseManager instance;
    private final HttpLogRepository httpLogRepository;
    private final LogRepository logRepository;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH);

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

    public long addHttpLogRequest(HttpLogRequest logRequest) {
        synchronized (LOCK) {
            return httpLogRepository.add(mapToLogModel(logRequest));
        }
    }

    public void addHttpLogResponse(HttpLogResponse logResponse) {
        synchronized (LOCK) {
            httpLogRepository.add(mapToLogModel(logResponse));
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

    private HttpLogModel mapToLogModel(HttpLogResponse response) {
        HttpLogModel httpLogModel = new HttpLogModel();
        httpLogModel.queryId = "id: " + response.queryId;
        httpLogModel.method = response.method;
        httpLogModel.time = dateFormat.format(response.time);
        httpLogModel.code = response.code;
        httpLogModel.message = response.message;
        httpLogModel.fullStatus = response.code + " " + response.message;
        httpLogModel.duration = response.duration == null ? null : response.duration + " ms";
        httpLogModel.bodySize = response.bodySize == null ? null : response.bodySize + " byte";
        httpLogModel.port = response.port;
        httpLogModel.ip = response.ip;
        httpLogModel.fullIpAddress = response.ip == null ? null : response.ip + ":" + response.port;
        httpLogModel.url = response.url;
        httpLogModel.errorMessage = response.errorMessage;
        httpLogModel.body = response.body;
        httpLogModel.queryType = QueryType.RESPONSE;

        httpLogModel.headers = new ArrayList<>();
        if (response.headers != null) {
            for (Map.Entry<String, String> header : response.headers.entrySet()) {
                httpLogModel.headers.add(header.getKey() + ": " + header.getValue());
            }
        }
        return httpLogModel;
    }

    private HttpLogModel mapToLogModel(HttpLogRequest request) {
        HttpLogModel httpLogModel = new HttpLogModel();
        httpLogModel.queryId = "id: " + request.queryId;
        httpLogModel.method = request.method;
        httpLogModel.time = dateFormat.format(request.time);
        httpLogModel.requestContentType = request.requestContentType;
        httpLogModel.bodySize = request.bodySize == null ? null : request.bodySize + " byte";
        httpLogModel.port = request.port;
        httpLogModel.ip = request.ip;
        httpLogModel.fullIpAddress = request.ip == null ? null : request.ip + ":" + request.port;
        httpLogModel.url = request.url;
        httpLogModel.body = request.body;
        httpLogModel.queryType = QueryType.REQUEST;

        httpLogModel.headers = new ArrayList<>();
        if (request.headers != null) {
            for (Map.Entry<String, String> header : request.headers.entrySet()) {
                httpLogModel.headers.add(header.getKey() + ": " + header.getValue());
            }
        }
        return httpLogModel;
    }
}
