package com.zerobranch.androidremotedebugger.source.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.zerobranch.androidremotedebugger.source.models.LogModel;

import java.util.ArrayList;
import java.util.List;

public final class LogRepository {
    private static final String REMOTE_LOGS_TABLE_NAME = "log_data";
    private final SQLiteDatabase database;

    public LogRepository(SQLiteDatabase database) {
        this.database = database;
    }

    public void addLog(LogModel model) {
        ContentValues values = new ContentValues();
        values.put(LogTable.TIME, model.time);
        values.put(LogTable.LEVEL, model.level);
        values.put(LogTable.TAG, model.tag);
        values.put(LogTable.MESSAGE, model.message);

        database.insert(REMOTE_LOGS_TABLE_NAME, null, values);
    }

    public List<LogModel> getLogsByFilter(int offset, int limit, String level, String tag, String search) {
        final List<String> arguments = new ArrayList<>();

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
            query.append(" lower (")
                    .append(LogTable.LEVEL)
                    .append(")")
                    .append(" like ")
                    .append("lower (?)");
            arguments.add("%".concat(level).concat("%"));
        }

        if (!TextUtils.isEmpty(tag)) {
            if (!TextUtils.isEmpty(level)) {
                query.append(" and ");
            }

            query.append(" lower (")
                    .append(LogTable.TAG)
                    .append(")")
                    .append(" like ")
                    .append("lower (?)");
            arguments.add(tag.concat("%"));
        }

        if (!TextUtils.isEmpty(search)) {
            if (!TextUtils.isEmpty(level) || !TextUtils.isEmpty(tag)) {
                query.append(" and ");
            }

            query.append(" (");

            String[] tables = new String[]{
                    LogTable.LEVEL,
                    LogTable.MESSAGE,
                    LogTable.TAG,
                    LogTable.TIME
            };

            for (int i = 0; i < tables.length; i++) {
                if (i != 0) {
                    query.append(" or ");
                }

                query.append(tables[i]).append(" like ?");
                arguments.add("%".concat(search).concat("%"));
            }
            query.append(") ");
        }

        query.append(" order by ")
                .append(LogTable.TIME)
                .append(" limit ")
                .append(limit);

        if (offset != -1) {
            query.append(" offset ").append(offset);
        }

        final Cursor cursor = database.rawQuery(query.toString(), arguments.toArray(new String[0]));
        final List<LogModel> logModels = new ArrayList<>();

        while (cursor.moveToNext()) {
            final LogModel log = new LogModel();
            log.time = cursor.getString(cursor.getColumnIndex(LogTable.TIME));
            log.level = cursor.getString(cursor.getColumnIndex(LogTable.LEVEL));
            log.tag = cursor.getString(cursor.getColumnIndex(LogTable.TAG));
            log.message = cursor.getString(cursor.getColumnIndex(LogTable.MESSAGE));
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
                LogTable.TIME + " text," +
                LogTable.LEVEL + " text," +
                LogTable.TAG + " text," +
                LogTable.MESSAGE + " text);";
        db.execSQL(query);
    }

    private interface LogTable {
        String ID = "_id";
        String TIME = "time";
        String LEVEL = "level";
        String TAG = "tag";
        String MESSAGE = "message";
    }
}
