/*
 * Copyright 2020 Arman Sargsyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zerobranch.androidremotedebugger.source.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zerobranch.androidremotedebugger.source.models.Table;
import zerobranch.androidremotedebugger.utils.FileUtils;

public final class DatabaseManager {
    private static final String FIELD_TYPE_NULL = "null";
    private static final String FIELD_TYPE_INTEGER = "integer";
    private static final String FIELD_TYPE_REAL = "real";
    private static final String FIELD_TYPE_BLOB = "blob";
    private static final String FIELD_TYPE_TEXT = "text";
    private static final String DATA_BASE_DIR = "databases";
    private static final String SELECT_QUERY = "select";
    private static final Object LOCK = new Object();

    private SQLiteDatabase db;
    private Context context;
    private static DatabaseManager instance;

    private DatabaseManager(Context context, String dbName) {
        this.context = context;

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

            customQuery = customQuery.trim();

            if (customQuery.toLowerCase().startsWith(SELECT_QUERY)) {
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

    public void removeItems(final String tableName, final List<String> headers, final List<List<String>> lines) {
        synchronized (LOCK) {
            transactionRun(() -> {
                StringBuilder whereClause = new StringBuilder();
                for (int i = 0; i < lines.size(); i++) {
                    List<String> arguments = new ArrayList<>();

                    for (int j = 0; j < lines.get(i).size(); j++) {
                        String item = lines.get(i).get(j);

                        if (!TextUtils.isEmpty(item)) {
                            whereClause.append(headers.get(j)).append("=?").append(" and ");
                            arguments.add(item);
                        }
                    }

                    whereClause.delete(whereClause.length() - 4, whereClause.length());

                    db.delete(tableName, whereClause.toString(), arguments.toArray(new String[0]));
                    whereClause.setLength(0);
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
            if (dbName == null) {
                return;
            }

            context.deleteDatabase(dbName);
            disconnect();
        }
    }

    public void updateData(String tableName, List<String> headers, List<String> oldValues, List<String> newValues) {
        synchronized (LOCK) {
            if (headers.size() != oldValues.size() || headers.size() != newValues.size()) {
                throw new IllegalArgumentException("the size of the array 'oldValues' " +
                    "and 'newValues' must match the size of the array 'headers'");
            }

            final StringBuilder whereClause = new StringBuilder();
            final ContentValues contentValues = new ContentValues();
            final List<String> arguments = new ArrayList<>();

            for (int i = 0; i < headers.size(); i++) {
                String oldVal = oldValues.get(i);
                String newVal = newValues.get(i);
                String header = headers.get(i);

                if (!newVal.equals(oldVal)) {
                    contentValues.put(header, newVal);
                }

                if (!TextUtils.isEmpty(header) && !TextUtils.isEmpty(oldVal)) {
                    whereClause.append(header).append("=?").append(" and ");
                    arguments.add(oldVal);
                }
            }

            if (contentValues.size() == 0) {
                return;
            }

            whereClause.delete(whereClause.length() - 4, whereClause.length());

            db.update(tableName, contentValues, whereClause.toString(), arguments.toArray(new String[0]));
        }
    }

    public Table search(String tableName, String text) {
        synchronized (LOCK) {
            final Table table = new Table();
            final String metaInfo = getTableMetaInfo(tableName);

            table.data = new ArrayList<>();
            table.headers = getHeaders(metaInfo);

            final StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");
            final List<String> arguments = new ArrayList<>();

            for (Table.Header header : table.headers) {
                queryBuilder.append(header.name)
                    .append(" LIKE ?")
                    .append(" or ");
                arguments.add("%".concat(text).concat("%"));
            }

            queryBuilder.delete(queryBuilder.length() - 4, queryBuilder.length() - 1);

            final Cursor cursor = db.rawQuery(queryBuilder.toString(), arguments.toArray(new String[0]));

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
            return null;
        }

        metadata = metadata.toLowerCase();

        String[] types = new String[]{FIELD_TYPE_INTEGER, FIELD_TYPE_REAL, FIELD_TYPE_BLOB, FIELD_TYPE_TEXT, FIELD_TYPE_NULL};

        for (String type : types) {
            if (metadata.contains(type)) {
                return type;
            }
        }
        return null;
    }

    private List<Table.Header> getImmutableHeaders(List<String> columnsName) {
        List<Table.Header> headers = new ArrayList<>();

        for (String columnName : columnsName) {
            headers.add(getDefaultHeader(columnName, false));
        }

        return headers;
    }

    private List<String> findPrimaryKey(String columnsText) {
        List<String> primaryKeys = new ArrayList<>();

        if (!columnsText.toLowerCase().contains("primary key")) {
            return primaryKeys;
        }

        Pattern pattern = Pattern.compile("(?i)(primary key\\s*\\().*?\\)");
        Matcher matcher = pattern.matcher(columnsText);
        if (matcher.find()) {
            String primaryKeyPart = matcher.group(0).replaceAll("(?i)(primary key\\s*\\()", "").replaceAll("\\)", "");
            String[] columnParams = primaryKeyPart.split(",");

            for (String columnParam : columnParams) {
                primaryKeys.add(columnParam.trim());
            }
        } else {
            String[] columnParams = columnsText.split(",");
            for (String columnParam : columnParams) {
                if (columnParam.toLowerCase().contains("primary key")) {
                    primaryKeys.add(columnParam.trim().split(" ")[0].trim());
                }
            }
        }

        return primaryKeys;
    }

    private List<Table.Header> getHeaders(String sql) {
        List<Table.Header> columnsList = new ArrayList<>();
        int start = sql.indexOf("(");
        int end = sql.lastIndexOf(")");

        String columnsText = sql.substring(start + 1, end);
        columnsText = columnsText
            .trim()
            .replaceAll("\"", "")
            .replaceAll("`", "")
            .replaceAll("'", "")
            .replaceAll("\n", " ")
            .replaceAll("\r", " ")
            .replaceAll("\t", " ");

        List<String> primaryKeys = findPrimaryKey(columnsText);

        columnsText = columnsText.replaceAll("(?i)(primary key\\s*\\().*?\\)", ""); // remove primary key in the end

        String[] columns = columnsText.split(",");

        for (String column : columns) {
            column = column.trim();
            String columnName = column.split(" ")[0].trim();

            if (columnName.isEmpty()) {
                continue;
            }

            if (column.equals(columnName)) {
                columnsList.add(getDefaultHeader(columnName, true));
                continue;
            }

            String columnMetadata = column.substring(columnName.length() + 1);
            String fieldType = getFieldType(columnMetadata);

            if (fieldType == null) {
                continue;
            }

            Table.Header header = new Table.Header();
            header.name = columnName;
            header.isMutable = !primaryKeys.contains(columnName);
            header.type = fieldType;

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

    private void transactionRun(Runnable runnable) {
        db.beginTransaction();
        try {
            runnable.run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private static void disconnect() {
        synchronized (LOCK) {
            if (instance != null) {
                if (instance.db != null && instance.db.isOpen()) {
                    instance.db.close();
                }

                instance.db = null;
                instance.context = null;
                instance = null;
            }
        }
    }
}