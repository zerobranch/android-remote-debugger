package com.sarproj.remotedebugger.source.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sarproj.remotedebugger.source.models.HttpLogModel;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class HttpLogRepository {
    private static final String REMOTE_NET_LOGS_TABLE_NAME = "net_log_data";
    private final SQLiteDatabase database;
    private final Gson gson = new Gson();

    public HttpLogRepository(SQLiteDatabase database) {
        this.database = database;
    }

    public HttpLogModel add(HttpLogModel model) {
        ContentValues values = new ContentValues();

        values.put(NetLogTable.CODE, model.code);
        values.put(NetLogTable.REQUEST_START_TIME, model.requestStartTime);
        values.put(NetLogTable.REQUEST_DURATION, model.requestDuration);
        values.put(NetLogTable.REQUEST_BODY_SIZE, model.requestBodySize);
        values.put(NetLogTable.RESPONSE_BODY_SIZE, model.responseBodySize);
        values.put(NetLogTable.PORT, model.port);
        values.put(NetLogTable.IS_COMPLETED_REQUEST, model.isCompletedRequest);
        values.put(NetLogTable.METHOD, model.method);
        values.put(NetLogTable.MESSAGE, model.message);
        values.put(NetLogTable.REQUEST_CONTENT_TYPE, model.requestContentType);
        values.put(NetLogTable.BASE_URL, model.baseUrl);
        values.put(NetLogTable.IP, model.ip);
        values.put(NetLogTable.FULL_URL, model.fullUrl);
        values.put(NetLogTable.SHORT_URL, model.shortUrl);
        values.put(NetLogTable.REQUEST_BODY, model.requestBody);
        values.put(NetLogTable.ERROR_MESSAGE, model.errorMessage);
        values.put(NetLogTable.RESPONSE_BODY, model.responseBody);
        putMap(values, NetLogTable.REQUEST_HEADERS, model.requestHeaders);
        putMap(values, NetLogTable.RESPONSE_HEADERS, model.responseHeaders);
        putMap(values, NetLogTable.QUERY_PARAMS, model.queryParams);

        database.insert(REMOTE_NET_LOGS_TABLE_NAME, null, values);
        return getLast();
    }

    public void update(HttpLogModel model) {
        ContentValues values = new ContentValues();

        values.put(NetLogTable.CODE, model.code);
        values.put(NetLogTable.REQUEST_START_TIME, model.requestStartTime);
        values.put(NetLogTable.REQUEST_DURATION, model.requestDuration);
        values.put(NetLogTable.REQUEST_BODY_SIZE, model.requestBodySize);
        values.put(NetLogTable.RESPONSE_BODY_SIZE, model.responseBodySize);
        values.put(NetLogTable.PORT, model.port);
        values.put(NetLogTable.IS_COMPLETED_REQUEST, model.isCompletedRequest);
        values.put(NetLogTable.METHOD, model.method);
        values.put(NetLogTable.MESSAGE, model.message);
        values.put(NetLogTable.REQUEST_CONTENT_TYPE, model.requestContentType);
        values.put(NetLogTable.BASE_URL, model.baseUrl);
        values.put(NetLogTable.IP, model.ip);
        values.put(NetLogTable.FULL_URL, model.fullUrl);
        values.put(NetLogTable.SHORT_URL, model.shortUrl);
        values.put(NetLogTable.REQUEST_BODY, model.requestBody);
        values.put(NetLogTable.ERROR_MESSAGE, model.errorMessage);
        values.put(NetLogTable.RESPONSE_BODY, model.responseBody);
        putMap(values, NetLogTable.REQUEST_HEADERS, model.requestHeaders);
        putMap(values, NetLogTable.RESPONSE_HEADERS, model.responseHeaders);
        putMap(values, NetLogTable.QUERY_PARAMS, model.queryParams);

        database.update(REMOTE_NET_LOGS_TABLE_NAME, values, NetLogTable.ID + "=" + model.id, null);
    }

    public void clearAll() {
        database.execSQL("delete from " + REMOTE_NET_LOGS_TABLE_NAME);
    }

    private HttpLogModel getLast() {
        HttpLogModel httpLogModel = new HttpLogModel();
        final String query = "select * from " + REMOTE_NET_LOGS_TABLE_NAME
                + " order by " + NetLogTable.ID + " desc "
                + " limit 1";

        final Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            httpLogModel.id = 0;
        } else {
            httpLogModel.id = cursor.getLong(cursor.getColumnIndex(NetLogTable.ID));
            httpLogModel.method = cursor.getString(cursor.getColumnIndex(NetLogTable.METHOD));
            httpLogModel.code = cursor.getInt(cursor.getColumnIndex(NetLogTable.CODE));
            httpLogModel.message = cursor.getString(cursor.getColumnIndex(NetLogTable.MESSAGE));
            httpLogModel.requestStartTime = cursor.getLong(cursor.getColumnIndex(NetLogTable.REQUEST_START_TIME));
            httpLogModel.requestDuration = cursor.getLong(cursor.getColumnIndex(NetLogTable.REQUEST_DURATION));
            httpLogModel.requestContentType = cursor.getString(cursor.getColumnIndex(NetLogTable.REQUEST_CONTENT_TYPE));
            httpLogModel.requestBodySize = cursor.getLong(cursor.getColumnIndex(NetLogTable.REQUEST_BODY_SIZE));
            httpLogModel.responseBodySize = cursor.getLong(cursor.getColumnIndex(NetLogTable.RESPONSE_BODY_SIZE));
            httpLogModel.baseUrl = cursor.getString(cursor.getColumnIndex(NetLogTable.BASE_URL));
            httpLogModel.port = cursor.getInt(cursor.getColumnIndex(NetLogTable.PORT));
            httpLogModel.ip = cursor.getString(cursor.getColumnIndex(NetLogTable.IP));
            httpLogModel.fullUrl = cursor.getString(cursor.getColumnIndex(NetLogTable.FULL_URL));
            httpLogModel.shortUrl = cursor.getString(cursor.getColumnIndex(NetLogTable.SHORT_URL));
            httpLogModel.requestBody = cursor.getString(cursor.getColumnIndex(NetLogTable.REQUEST_BODY));
            httpLogModel.errorMessage = cursor.getString(cursor.getColumnIndex(NetLogTable.ERROR_MESSAGE));
            httpLogModel.responseBody = cursor.getString(cursor.getColumnIndex(NetLogTable.RESPONSE_BODY));
            httpLogModel.isCompletedRequest = cursor.getInt(cursor.getColumnIndex(NetLogTable.IS_COMPLETED_REQUEST)) == 1;

            Type empMapType = new TypeToken<HashMap<String, String>>() {}.getType();
            httpLogModel.requestHeaders = gson.fromJson(cursor.getString(cursor.getColumnIndex(NetLogTable.REQUEST_HEADERS)), empMapType);
            httpLogModel.responseHeaders = gson.fromJson(cursor.getString(cursor.getColumnIndex(NetLogTable.RESPONSE_HEADERS)), empMapType);
            httpLogModel.queryParams = gson.fromJson(cursor.getString(cursor.getColumnIndex(NetLogTable.QUERY_PARAMS)), empMapType);
        }

        cursor.close();
        return httpLogModel;
    }

    public void createHttpLogsTable(SQLiteDatabase db) {
        final String query = "create table " + REMOTE_NET_LOGS_TABLE_NAME + " (" +
                NetLogTable.ID + " integer primary key autoincrement, " +
                NetLogTable.METHOD + " text," +
                NetLogTable.CODE + " text," +
                NetLogTable.MESSAGE + " text," +
                NetLogTable.REQUEST_START_TIME + " integer," +
                NetLogTable.REQUEST_DURATION + " integer," +
                NetLogTable.REQUEST_CONTENT_TYPE + " text," +
                NetLogTable.REQUEST_BODY_SIZE + " integer," +
                NetLogTable.RESPONSE_BODY_SIZE + " integer," +
                NetLogTable.BASE_URL + " text," +
                NetLogTable.PORT + " integer," +
                NetLogTable.IP + " text," +
                NetLogTable.FULL_URL + " text," +
                NetLogTable.SHORT_URL + " text," +
                NetLogTable.REQUEST_BODY + " text," +
                NetLogTable.ERROR_MESSAGE + " text," +
                NetLogTable.RESPONSE_BODY + " text," +
                NetLogTable.REQUEST_HEADERS + " text," +
                NetLogTable.RESPONSE_HEADERS + " text," +
                NetLogTable.IS_COMPLETED_REQUEST + " integer," +
                NetLogTable.QUERY_PARAMS + " text);";
        db.execSQL(query);
    }

    private void putMap(ContentValues values, String field, Map<String, String> map) {
        String val = null;
        if (map != null) {
            val = gson.toJson(map);
        }

        values.put(field, val);
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
        String IS_COMPLETED_REQUEST = "is_completed_request";
    }
}
