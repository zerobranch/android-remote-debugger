package com.sarproj.remotedebugger.source.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogModel;
import com.sarproj.remotedebugger.source.models.httplog.QueryType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpLogRepository {
    private static final String REMOTE_NET_LOGS_TABLE_NAME = "net_log_data";
    private final SQLiteDatabase database;
    private final Gson gson = new Gson();

    public HttpLogRepository(SQLiteDatabase database) {
        this.database = database;
    }

    public long addWithAutoQueryId(HttpLogModel model) {
        try {
            database.beginTransaction();
            ContentValues values = new ContentValues();

            values.put(NetLogTable.CODE, model.code);
            values.put(NetLogTable.REQUEST_START_TIME, model.requestStartTime);
            values.put(NetLogTable.REQUEST_DURATION, model.requestDuration);
            values.put(NetLogTable.REQUEST_BODY_SIZE, model.requestBodySize);
            values.put(NetLogTable.RESPONSE_BODY_SIZE, model.responseBodySize);
            values.put(NetLogTable.PORT, model.port);
            values.put(NetLogTable.METHOD, sqlFormat(model.method));
            values.put(NetLogTable.QUERY_TYPE, sqlFormat(model.queryType.name()));
            values.put(NetLogTable.MESSAGE, sqlFormat(model.message));
            values.put(NetLogTable.REQUEST_CONTENT_TYPE, sqlFormat(model.requestContentType));
            values.put(NetLogTable.BASE_URL, sqlFormat(model.baseUrl));
            values.put(NetLogTable.IP, sqlFormat(model.ip));
            values.put(NetLogTable.FULL_URL, sqlFormat(model.fullUrl));
            values.put(NetLogTable.SHORT_URL, sqlFormat(model.shortUrl));
            values.put(NetLogTable.REQUEST_BODY, sqlFormat(model.requestBody));
            values.put(NetLogTable.ERROR_MESSAGE, sqlFormat(model.errorMessage));
            values.put(NetLogTable.RESPONSE_BODY, sqlFormat(model.responseBody));
            putMap(values, NetLogTable.REQUEST_HEADERS, model.requestHeaders);
            putMap(values, NetLogTable.RESPONSE_HEADERS, model.responseHeaders);
            putMap(values, NetLogTable.QUERY_PARAMS, model.queryParams);

            long id = database.insert(REMOTE_NET_LOGS_TABLE_NAME, null, values);

            model.queryId = id;

            values.clear();
            values.put(NetLogTable.QUERY_ID, model.queryId);
            database.update(REMOTE_NET_LOGS_TABLE_NAME, values, NetLogTable.ID + "=" + id, null);
            database.setTransactionSuccessful();

            return id;
        } finally {
            database.endTransaction();
        }
    }

    public void add(HttpLogModel model) {
        ContentValues values = new ContentValues();

        values.put(NetLogTable.CODE, model.code);
        values.put(NetLogTable.REQUEST_START_TIME, model.requestStartTime);
        values.put(NetLogTable.REQUEST_DURATION, model.requestDuration);
        values.put(NetLogTable.REQUEST_BODY_SIZE, model.requestBodySize);
        values.put(NetLogTable.RESPONSE_BODY_SIZE, model.responseBodySize);
        values.put(NetLogTable.QUERY_ID, model.queryId);
        values.put(NetLogTable.PORT, model.port);
        values.put(NetLogTable.METHOD, sqlFormat(model.method));
        values.put(NetLogTable.QUERY_TYPE, sqlFormat(model.queryType.name()));
        values.put(NetLogTable.MESSAGE, sqlFormat(model.message));
        values.put(NetLogTable.REQUEST_CONTENT_TYPE, sqlFormat(model.requestContentType));
        values.put(NetLogTable.BASE_URL, sqlFormat(model.baseUrl));
        values.put(NetLogTable.IP, sqlFormat(model.ip));
        values.put(NetLogTable.FULL_URL, sqlFormat(model.fullUrl));
        values.put(NetLogTable.SHORT_URL, sqlFormat(model.shortUrl));
        values.put(NetLogTable.REQUEST_BODY, sqlFormat(model.requestBody));
        values.put(NetLogTable.ERROR_MESSAGE, sqlFormat(model.errorMessage));
        values.put(NetLogTable.RESPONSE_BODY, sqlFormat(model.responseBody));
        putMap(values, NetLogTable.REQUEST_HEADERS, model.requestHeaders);
        putMap(values, NetLogTable.RESPONSE_HEADERS, model.responseHeaders);
        putMap(values, NetLogTable.QUERY_PARAMS, model.queryParams);

        database.insert(REMOTE_NET_LOGS_TABLE_NAME, null, values);
    }

    public void clearAll() {
        database.delete(REMOTE_NET_LOGS_TABLE_NAME, null, null);
    }

    public void createHttpLogsTable(SQLiteDatabase db) {
        final String query = "create table " + REMOTE_NET_LOGS_TABLE_NAME + " (" +
                NetLogTable.ID + " integer primary key autoincrement, " +
                NetLogTable.QUERY_ID + " integer, " +
                NetLogTable.METHOD + " text," +
                NetLogTable.CODE + " text," +
                NetLogTable.MESSAGE + " text," +
                NetLogTable.QUERY_TYPE + " text," +
                NetLogTable.REQUEST_START_TIME + " integer," +
                NetLogTable.REQUEST_DURATION + " integer," +
                NetLogTable.REQUEST_CONTENT_TYPE + " text," +
                NetLogTable.REQUEST_BODY_SIZE + " integer," +
                NetLogTable.RESPONSE_BODY_SIZE + " integer," +
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

    private void putMap(ContentValues values, String field, Map<String, String> map) {
        String val = null;
        if (map != null) {
            val = sqlFormat(gson.toJson(map));
        }

        values.put(field, val);
    }

    private String sqlFormat(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("'", "&shadow_39&");
    }

    private String getValidString(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("&shadow_39&", "'");
    }

    public List<HttpLogModel> getHttpLogs(String level, String search) {
        final StringBuilder query = new StringBuilder()
                .append("select * from " + REMOTE_NET_LOGS_TABLE_NAME);

//        if (!TextUtils.isEmpty(level) || !TextUtils.isEmpty(search)) {
//            query.append(" where ");
//        }

        if (!TextUtils.isEmpty(level)) {
//            query.append(LogRepository.LogTable.LEVEL)
//                    .append(" = ")
//                    .append("'")
//                    .append(level)
//                    .append("'");
        }

        if (!TextUtils.isEmpty(search)) {
//            if (!TextUtils.isEmpty(level)) {
//                query.append(" and ");
//            }

//            query.append(LogRepository.LogTable.MESSAGE)
//                    .append(" like ")
//                    .append("'%")
//                    .append(search)
//                    .append("%'");
        }

        query.append(" order by ").append(NetLogTable.ID);

        final Cursor cursor = database.rawQuery(query.toString(), null);
        final List<HttpLogModel> logModels = new ArrayList<>();

        while (cursor.moveToNext()) {
            HttpLogModel httpLogModel = new HttpLogModel();
            httpLogModel.id = cursor.getLong(cursor.getColumnIndex(NetLogTable.ID));
            httpLogModel.method = getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.METHOD)));
            httpLogModel.queryId = cursor.getLong(cursor.getColumnIndex(NetLogTable.QUERY_ID));
            httpLogModel.queryType = QueryType.valueOf(cursor.getString(cursor.getColumnIndex(NetLogTable.QUERY_TYPE)));
            httpLogModel.code = cursor.getString(cursor.getColumnIndex(NetLogTable.CODE));
            httpLogModel.message = getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.MESSAGE)));
            httpLogModel.requestStartTime = cursor.getLong(cursor.getColumnIndex(NetLogTable.REQUEST_START_TIME));
            httpLogModel.requestDuration = cursor.getLong(cursor.getColumnIndex(NetLogTable.REQUEST_DURATION));
            httpLogModel.requestContentType = getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.REQUEST_CONTENT_TYPE)));
            httpLogModel.requestBodySize = cursor.getLong(cursor.getColumnIndex(NetLogTable.REQUEST_BODY_SIZE));
            httpLogModel.responseBodySize = cursor.getLong(cursor.getColumnIndex(NetLogTable.RESPONSE_BODY_SIZE));
            httpLogModel.baseUrl = getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.BASE_URL)));
            httpLogModel.port = cursor.getString(cursor.getColumnIndex(NetLogTable.PORT));
            httpLogModel.ip = getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.IP)));
            httpLogModel.fullUrl = getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.FULL_URL)));
            httpLogModel.shortUrl = getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.SHORT_URL)));
            httpLogModel.requestBody = getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.REQUEST_BODY)));
            httpLogModel.errorMessage = getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.ERROR_MESSAGE)));
            httpLogModel.responseBody = getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.RESPONSE_BODY)));

            Type empMapType = new TypeToken<HashMap<String, String>>() {}.getType();
            httpLogModel.requestHeaders = gson.fromJson(getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.REQUEST_HEADERS))), empMapType);
            httpLogModel.responseHeaders = gson.fromJson(getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.RESPONSE_HEADERS))), empMapType);
            httpLogModel.queryParams = gson.fromJson(getValidString(cursor.getString(cursor.getColumnIndex(NetLogTable.QUERY_PARAMS))), empMapType);

            logModels.add(httpLogModel);
        }

        cursor.close();
        return logModels;
    }

    private interface NetLogTable {
        String ID = "_id";
        String QUERY_ID = "query_id";
        String QUERY_TYPE = "query_type";
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