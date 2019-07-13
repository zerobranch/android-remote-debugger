package com.sarproj.remotedebugger.source.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.sarproj.remotedebugger.source.models.LogModel;

import java.util.ArrayList;
import java.util.List;

public final class LogRepository {
    private static final int LIMIT_LOGS_PACKS = 1000;
    private static final String REMOTE_LOGS_TABLE_NAME = "log_data";
    private final SQLiteDatabase database;

    public LogRepository(SQLiteDatabase database) {
        this.database = database;
    }

    public void addLog(LogModel model) {
        ContentValues values = new ContentValues();
        values.put(LogTable.TIME, model.getNewTime());
        values.put(LogTable.LEVEL, sqlFormat(model.level));
        values.put(LogTable.TAG, sqlFormat(model.tag));
        values.put(LogTable.MESSAGE, sqlFormat(model.message));

        database.insert(REMOTE_LOGS_TABLE_NAME, null, values);
    }

    public List<LogModel> getLogsByFilter(int offset, String level, String tag, String search) {
        final StringBuilder query = new StringBuilder()
                .append("select ")
                .append(LogTable.TIME + ", ")
                .append(LogTable.LEVEL + ", ")
                .append(LogTable.TAG + ", ")
                .append(LogTable.MESSAGE)
                .append(" from " + REMOTE_LOGS_TABLE_NAME);

        if (!TextUtils.isEmpty(level) || !TextUtils.isEmpty(tag) || !TextUtils.isEmpty(search)) {
            query.append(" where ");
        }

        if (!TextUtils.isEmpty(level)) {
            query.append(LogTable.LEVEL)
                    .append(" = ")
                    .append("'")
                    .append(level)
                    .append("'");
        }

        if (!TextUtils.isEmpty(tag)) {
            if (!TextUtils.isEmpty(level)) {
                query.append(" and ");
            }

            query.append(" lower (")
                    .append(LogTable.TAG)
                    .append(")")
                    .append(" like ")
                    .append("lower ('")
                    .append(tag)
                    .append("%')");
        }

        if (!TextUtils.isEmpty(search)) {
            if (!TextUtils.isEmpty(level) || !TextUtils.isEmpty(tag)) {
                query.append(" and ");
            }

            query.append(LogTable.MESSAGE)
                    .append(" like ")
                    .append("'%")
                    .append(search)
                    .append("%'");
        }

        query.append(" order by ")
                .append(LogTable.TIME)
                .append(" limit ")
                .append(LIMIT_LOGS_PACKS);

        if (offset != -1) {
            query.append(" offset ").append(offset);
        }

        final Cursor cursor = database.rawQuery(query.toString(), null);
        final List<LogModel> logModels = new ArrayList<>();

        while (cursor.moveToNext()) {
            final LogModel log = new LogModel();
            log.time = cursor.getLong(cursor.getColumnIndex(LogTable.TIME));
            log.level = getValidString(cursor.getString(cursor.getColumnIndex(LogTable.LEVEL)));
            log.tag = getValidString(cursor.getString(cursor.getColumnIndex(LogTable.TAG)));
            log.message = getValidString(cursor.getString(cursor.getColumnIndex(LogTable.MESSAGE)));
            logModels.add(log);
        }

        cursor.close();
        return logModels;
    }

    public void clearAllLogs() {
        database.delete(REMOTE_LOGS_TABLE_NAME, null, null);
    }

    public void createLogsTable(SQLiteDatabase db) {
        final String query = "create table " + REMOTE_LOGS_TABLE_NAME + " (" +
                LogTable.ID + " integer primary key autoincrement, " +
                LogTable.TIME + " integer," +
                LogTable.LEVEL + " text," +
                LogTable.TAG + " text," +
                LogTable.MESSAGE + " text);";
        db.execSQL(query);
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

    private interface LogTable {
        String ID = "_id";
        String TIME = "time";
        String LEVEL = "level";
        String TAG = "tag";
        String MESSAGE = "message";
    }
}
