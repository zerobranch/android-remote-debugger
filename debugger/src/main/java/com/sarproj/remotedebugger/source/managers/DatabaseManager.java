package com.sarproj.remotedebugger.source.managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.sarproj.remotedebugger.source.models.Table;
import com.sarproj.remotedebugger.utils.FileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class DatabaseManager {
    private static final String FIELD_TYPE_NULL = "null";
    private static final String FIELD_TYPE_INTEGER = "integer";
    private static final String FIELD_TYPE_REAL = "real";
    private static final String FIELD_TYPE_BLOB = "blob";
    private static final String FIELD_TYPE_TEXT = "text";
    private static final String DATA_BASE_DIR = "databases";
    private static final String SELECT_QUERY = "select";
    private static final String PRIMARY_KEY = "primary key";
    private static final Object LOCK = new Object();

    private SQLiteDatabase db;
    private WeakReference<Context> contextWeakReference;
    private static DatabaseManager instance;

    private DatabaseManager(Context context, String dbName) {
        contextWeakReference = new WeakReference<>(context);

        File dataBaseDir = new File(context.getApplicationInfo().dataDir, DATA_BASE_DIR);
        if (!dataBaseDir.exists() || !dataBaseDir.isDirectory()) {
            throw new RuntimeException("Database directory not found");
        }

        File dbFile = FileUtils.searchFile(dataBaseDir, dbName);
        if (dbFile == null) {
            throw new RuntimeException("Database '" + dbName + "' not found");
        }

        db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    public static DatabaseManager getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException("The database is not open. " +
                        "Please, use '" + DatabaseManager.class.getName() + ".connect(Context, String)'");
            }
            return instance;
        }
    }

    public static void connect(Context context, String dbName) {
        synchronized (LOCK) {
            disconnect();
            instance = new DatabaseManager(context, dbName);
        }
    }

    public static void disconnect() {
        synchronized (LOCK) {
            if (instance != null) {
                if (instance.db != null && instance.db.isOpen()) {
                    instance.db.close();
                }

                instance.db = null;
                instance.contextWeakReference.clear();
                instance.contextWeakReference = null;
                instance = null;
            }
        }
    }

    public static List<String> getDBNameList(Context context) {
        synchronized (LOCK) {
            final File dataBaseDir = new File(context.getApplicationInfo().dataDir, DATA_BASE_DIR);

            if (!dataBaseDir.exists() || !dataBaseDir.isDirectory()) {
                return Collections.emptyList();
            }

            return FileUtils.getDBFilesNames(dataBaseDir);
        }
    }

    public int getDatabaseVersion() {
        synchronized (LOCK) {
            return db.getVersion();
        }
    }

    public List<String> getTables() {
        synchronized (LOCK) {
            final Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);
            List<String> tables = new ArrayList<>();
            while (cursor.moveToNext()) {
                tables.add(cursor.getString(0));
            }

            cursor.close();
            return tables;
        }
    }

    public Table getTableDataByQuery(String customQuery) {
        synchronized (LOCK) {
            final Table table = new Table();
            table.data = new ArrayList<>();

            customQuery = customQuery.trim().toLowerCase();

            if (customQuery.startsWith(SELECT_QUERY)) {
                final Cursor cursor = db.rawQuery(customQuery, null);
                table.headers = getImmutableHeaders(Arrays.asList(cursor.getColumnNames()));
                table.count = cursor.getCount();

                while (cursor.moveToNext()) {
                    final List<String> row = new ArrayList<>();
                    for (Table.Header column : table.headers) {
                        row.add(cursor.getString(cursor.getColumnIndex(column.name)));
                    }
                    table.data.add(row);
                }

                cursor.close();
            } else {
                table.count = 0;
                table.headers = Collections.emptyList();

                db.execSQL(customQuery);
            }

            return table;
        }
    }

    public int getTableDataCount(String tableName) {
        synchronized (LOCK) {
            final Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + tableName, null);
            cursor.moveToFirst();
            final int count = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(0)));
            cursor.close();
            return count;
        }
    }

    public Table getTableData(String tableName, int page, int limit) {
        synchronized (LOCK) {
            final Table table = new Table();
            final String metaInfo = getTableMetaInfo(tableName);

            table.data = new ArrayList<>();
            table.headers = getHeaders(metaInfo);

            final int offset = (page - 1) * limit;
            final Cursor cursor = db.rawQuery("SELECT * FROM " + tableName +
                    " LIMIT " + limit + " OFFSET " + offset, null);

            while (cursor.moveToNext()) {
                final List<String> row = new ArrayList<>();
                for (Table.Header column : table.headers) {
                    row.add(cursor.getString(cursor.getColumnIndex(column.name)));
                }
                table.data.add(row);
            }

            cursor.close();
            return table;
        }
    }

    public void removeItems(final String tableName, List<String> headers, final List<List<String>> lines) {
        synchronized (LOCK) {
            final StringBuilder whereClause = new StringBuilder();
            for (String header : headers) {
                whereClause.append(header).append("=?").append(" and ");
            }

            whereClause.delete(whereClause.length() - 4, whereClause.length());

            transactionRun(new Runnable() {
                @Override
                public void run() {
                    for (List<String> line : lines) {
                        db.delete(tableName, whereClause.toString(), line.toArray(new String[0]));
                    }
                }
            });
        }
    }

    public void dropTable(String tableName) {
        synchronized (LOCK) {
            if (tableName == null) {
                return;
            }
            db.execSQL("DROP TABLE " + tableName);
        }
    }

    public void dropDatabase(String dbName) {
        synchronized (LOCK) {
            if (dbName == null || contextWeakReference.get() == null) {
                return;
            }

            contextWeakReference.get().deleteDatabase(dbName);
            disconnect();
        }
    }

    public void updateData(String tableName, List<String> headers, List<String> oldValues, List<String> newValues) {
        synchronized (LOCK) {
            if (headers.size() != oldValues.size() || headers.size() != newValues.size()) {
                throw new IllegalArgumentException("the size of the array 'oldValues' " +
                        "and 'newValues' must match the size of the array 'headers'");
            }

            final StringBuilder queryForUpdate = new StringBuilder();
            final StringBuilder whereClause = new StringBuilder();

            for (int i = 0; i < headers.size(); i++) {
                String oldVal = oldValues.get(i);
                String newVal = newValues.get(i);
                String header = headers.get(i);

                if (!newVal.equals(oldVal)) {
                    queryForUpdate.append(header)
                            .append(" = ")
                            .append((newVal.isEmpty()) ? null : "'".concat(newVal).concat("'"))
                            .append(", ");
                }

                if (!TextUtils.isEmpty(header)) {
                    whereClause.append(header)
                            .append(" = '")
                            .append(oldVal)
                            .append("' and ");
                }
            }

            if (queryForUpdate.length() == 0) {
                return;
            }

            queryForUpdate.delete(queryForUpdate.length() - 2, queryForUpdate.length());
            whereClause.delete(whereClause.length() - 4, whereClause.length());

            db.execSQL("UPDATE " + tableName + " SET " + queryForUpdate.toString() + " WHERE " + whereClause.toString());
        }
    }

    public Table search(String tableName, String text) {
        synchronized (LOCK) {
            final Table table = new Table();
            final String metaInfo = getTableMetaInfo(tableName);

            table.data = new ArrayList<>();
            table.headers = getHeaders(metaInfo);

            final StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");

            for (Table.Header header : table.headers) {
                queryBuilder.append(header.name)
                        .append(" LIKE ")
                        .append("'%")
                        .append(text)
                        .append("%'")
                        .append(" or ");
            }

            queryBuilder.delete(queryBuilder.length() - 4, queryBuilder.length() - 1);

            final Cursor cursor = db.rawQuery(queryBuilder.toString(), null);

            while (cursor.moveToNext()) {
                final List<String> row = new ArrayList<>();
                for (Table.Header column : table.headers) {
                    row.add(cursor.getString(cursor.getColumnIndex(column.name)));
                }
                table.data.add(row);
            }

            table.count = cursor.getCount();
            cursor.close();
            return table;
        }
    }

    private String getTableMetaInfo(String tableName) {
        final Cursor metaCursor = db.rawQuery("SELECT sql FROM sqlite_master " +
                "where name = '" + tableName + "'", null);

        metaCursor.moveToFirst();
        String metaSql = metaCursor.getString(metaCursor.getColumnIndex("sql"));
        metaCursor.close();
        return metaSql;
    }

    private String getFieldType(String metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return FIELD_TYPE_TEXT;
        }

        String type = metadata.split(" ")[0].toLowerCase();

        if (type.contains(FIELD_TYPE_INTEGER)) {
            return FIELD_TYPE_INTEGER;
        } else if (type.contains(FIELD_TYPE_REAL)) {
            return FIELD_TYPE_REAL;
        } else if (type.contains(FIELD_TYPE_BLOB)) {
            return FIELD_TYPE_BLOB;
        } else if (type.contains(FIELD_TYPE_NULL)) {
            return FIELD_TYPE_NULL;
        }

        return FIELD_TYPE_TEXT;
    }

    private List<Table.Header> getImmutableHeaders(List<String> columnsName) {
        List<Table.Header> headers = new ArrayList<>();

        for (String columnName : columnsName) {
            headers.add(getDefaultHeader(columnName, false));
        }

        return headers;
    }

    private List<Table.Header> getHeaders(String sql) {
        List<Table.Header> columnsList = new ArrayList<>();
        int start = sql.indexOf("(");
        int end = sql.indexOf(")");
        String[] columns = sql.substring(start + 1, end).split(",");

        for (String column : columns) {
            column = column.trim().replaceAll("\"", "").replaceAll("`", "");
            String columnName = column.split(" ")[0];

            if (column.length() == columnName.length()) {
                columnsList.add(getDefaultHeader(columnName, true));
                continue;
            }

            String columnMetadata = column.substring(columnName.length() + 1);
            columnMetadata = columnMetadata.toLowerCase();

            Table.Header header = new Table.Header();
            header.name = columnName;
            header.isMutable = !isPrimaryKey(columnMetadata);
            header.type = getFieldType(columnMetadata);

            columnsList.add(header);
        }

        return columnsList;
    }

    private Table.Header getDefaultHeader(String columnName, boolean isMutable) {
        Table.Header header = new Table.Header();
        header.name = columnName;
        header.isMutable = isMutable;
        header.type = FIELD_TYPE_TEXT;
        return header;
    }

    private boolean isPrimaryKey(String metadata) {
        return metadata.toLowerCase().contains(PRIMARY_KEY);
    }

    private void transactionRun(Runnable runnable) {
        db.beginTransaction();
        try {
            runnable.run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}