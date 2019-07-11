package com.sarproj.remotedebugger.source.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.sarproj.remotedebugger.source.models.HttpLogModel;

public class HttpLogRepository {
    private static final int LIMIT_NET_LOGS_PACKS = 1000;
    private static final String REMOTE_NET_LOGS_TABLE_NAME = "net_log_data";
    private final SQLiteDatabase database;
    private final Gson gson = new Gson();

    public HttpLogRepository(SQLiteDatabase database) {
        this.database = database;
    }

    public void add(HttpLogModel model) {
        final String query = "insert or replace into " + REMOTE_NET_LOGS_TABLE_NAME + " (" +
                NetLogTable.METHOD + ", " +
                NetLogTable.CODE + ", " +
                NetLogTable.MESSAGE + ", " +
                NetLogTable.REQUEST_START_TIME + ", " +
                NetLogTable.REQUEST_DURATION + ", " +
                NetLogTable.REQUEST_CONTENT_TYPE + ", " +
                NetLogTable.REQUEST_BODY_SIZE + ", " +
                NetLogTable.RESPONSE_BODY_SIZE + ", " +
                NetLogTable.BASE_URL + ", " +
                NetLogTable.PORT + ", " +
                NetLogTable.IP + ", " +
                NetLogTable.FULL_URL + ", " +
                NetLogTable.SHORT_URL + ", " +
                NetLogTable.REQUEST_BODY + ", " +
                NetLogTable.ERROR_MESSAGE + ", " +
                NetLogTable.RESPONSE_BODY + ", " +
                NetLogTable.REQUEST_HEADERS + ", " +
                NetLogTable.RESPONSE_HEADERS + ", " +
                NetLogTable.QUERY_PARAMS + ") " +
                "values (" +
                "'" + model.method + "', " +
                "'" + model.code + "', " +
                "'" + model.message + "', " +
                "'" + model.requestStartTime + "', " +
                "'" + model.requestDuration + "', " +
                "'" + model.requestContentType + "', " +
                "'" + model.requestBodySize + "', " +
                "'" + model.responseBodySize + "', " +
                "'" + model.baseUrl + "', " +
                "'" + model.port + "', " +
                "'" + model.ip + "', " +
                "'" + model.fullUrl + "', " +
                "'" + model.shortUrl + "', " +
                "'" + model.requestBody + "', " +
                "'" + model.errorMessage + "', " +
                "'" + model.responseBody + "', " +
                "'" + gson.toJson(model.requestHeaders) + "', " +
                "'" + gson.toJson(model.responseHeaders) + "', " +
                "'" + gson.toJson(model.queryParams) + "');";
        database.execSQL(query);
    }

    public void clearAll() {
        database.execSQL("delete from " + REMOTE_NET_LOGS_TABLE_NAME);
    }

    public long getLastId(long defaultValue) {
        final String query = "select " + NetLogTable.ID
                + " from " + REMOTE_NET_LOGS_TABLE_NAME
                + " order by " + NetLogTable.ID + " desc "
                + " limit 1";

        final Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        long id = (cursor.getCount() == 0) ? defaultValue : cursor.getLong(cursor.getColumnIndex(NetLogTable.ID));
        cursor.close();
        return id;
    }

    public void createHttpLogsTable(SQLiteDatabase db) {
        final String query = "create table " + REMOTE_NET_LOGS_TABLE_NAME + " (" +
                NetLogTable.ID + " integer primary key autoincrement, " +
                NetLogTable.METHOD + " text," +
                NetLogTable.CODE + " text," +
                NetLogTable.MESSAGE + " text," +
                NetLogTable.REQUEST_START_TIME + " text," +
                NetLogTable.REQUEST_DURATION + " text," +
                NetLogTable.REQUEST_CONTENT_TYPE + " text," +
                NetLogTable.REQUEST_BODY_SIZE + " text," +
                NetLogTable.RESPONSE_BODY_SIZE + " text," +
                NetLogTable.BASE_URL + " text," +
                NetLogTable.PORT + " text," +
                NetLogTable.IP + " text," +
                NetLogTable.FULL_URL + " text," +
                NetLogTable.SHORT_URL + " text," +
                NetLogTable.REQUEST_BODY + " text," +
                NetLogTable.ERROR_MESSAGE + " text," +
                NetLogTable.RESPONSE_BODY + " text," +
                NetLogTable.REQUEST_HEADERS + " text," +
                NetLogTable.RESPONSE_HEADERS + " text," +
                NetLogTable.QUERY_PARAMS + " text);";
        db.execSQL(query);
    }

    private interface NetLogTable {
        String ID = "_id";
        String METHOD = "method";
        String CODE = "code";
        String MESSAGE = "message";
        String REQUEST_START_TIME = "request_start_time";
        String REQUEST_DURATION = "request_duration";
        String REQUEST_CONTENT_TYPE = "request_content_type";
        String REQUEST_BODY_SIZE = "request_body_size";
        String RESPONSE_BODY_SIZE = "response_body_size";
        String BASE_URL = "base_url";
        String PORT = "port";
        String IP = "ip";
        String FULL_URL = "full_url";
        String SHORT_URL = "short_url";
        String REQUEST_BODY = "request_body";
        String ERROR_MESSAGE = "error_message";
        String RESPONSE_BODY = "response_body";
        String REQUEST_HEADERS = "request_headers";
        String RESPONSE_HEADERS = "response_headers";
        String QUERY_PARAMS = "query_params";
    }
}
