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
package zerobranch.androidremotedebugger.api.database;

import android.content.Context;

import zerobranch.androidremotedebugger.api.base.Controller;
import zerobranch.androidremotedebugger.api.base.HtmlParams;
import zerobranch.androidremotedebugger.http.Host;
import zerobranch.androidremotedebugger.settings.InternalSettings;
import zerobranch.androidremotedebugger.source.managers.DatabaseManager;
import zerobranch.androidremotedebugger.source.models.DeletingDatabase;
import zerobranch.androidremotedebugger.source.models.Table;
import zerobranch.androidremotedebugger.source.models.Tables;
import zerobranch.androidremotedebugger.source.models.UpdatingDatabase;
import zerobranch.androidremotedebugger.utils.FileUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.ResponseException;

public class DatabaseController extends Controller {
    private static final int FIRST_PAGE = 1;
    private static final int LAST_PAGE = -1;
    private static final int DEFAULT_PAGE_SIZE = 15;

    public DatabaseController(Context context, InternalSettings internalSettings) {
        super(context, internalSettings);
    }

    @Override
    public String execute(Map<String, List<String>> params) throws ResponseException {
        if (params == null || params.isEmpty()) {
            return FileUtils.getTextFromAssets(context.getAssets(), Host.DATABASE.getPath());
        } else if (params.containsKey(DatabaseHtmlKey.GET_DATABASES)) {
            return getDatabases();
        } else if (params.containsKey(DatabaseHtmlKey.GET_TABLES)) {
            return getTables(params);
        } else if (params.containsKey(DatabaseHtmlKey.GET_TABLE)) {
            return getTable(params);
        } else if (params.containsKey(DatabaseHtmlKey.UPDATE_TABLE)) {
            return updateTable(params);
        } else if (params.containsKey(DatabaseHtmlKey.DELETE_TABLE_ITEMS)) {
            return deleteTableItems(params);
        } else if (params.containsKey(DatabaseHtmlKey.DROP_DATABASE)) {
            return dropDatabase(params);
        } else if (params.containsKey(DatabaseHtmlKey.DROP_TABLE)) {
            return dropTable(params);
        } else if (params.containsKey(DatabaseHtmlKey.GET_BY_QUERY)) {
            return getByQuery(params);
        } else if (params.containsKey(DatabaseHtmlKey.SEARCH)) {
            return search(params);
        }

        return EMPTY;
    }

    private String getTable(Map<String, List<String>> params) throws ResponseException {
        if (notContains(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final String tableName = getStringValue(params, HtmlParams.NAME);
        int tablePage = getIntValue(params, HtmlParams.PAGE, FIRST_PAGE);
        int pageSize = getIntValue(params, HtmlParams.SIZE, DEFAULT_PAGE_SIZE);

        final int tablesCount = getDBAccess().getTableDataCount(tableName);

        if (tablePage == LAST_PAGE) {
            tablePage = (int) Math.ceil((double) tablesCount / pageSize);
        }

        final Table table = getDBAccess().getTableData(tableName, tablePage, pageSize);
        table.count = tablesCount;
        return serialize(table);
    }

    private String getTables(Map<String, List<String>> params) throws ResponseException {
        if (notContains(params, HtmlParams.DATABASE)) {
            throwEmptyParameterException(HtmlParams.DATABASE);
        }

        final String dbName = getStringValue(params, HtmlParams.DATABASE);
        DatabaseManager.connect(context, dbName);

        List<String> tables = getDBAccess().getTables();
        Collections.sort(tables, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });

        return serialize(new Tables(tables, getDBAccess().getDatabaseVersion()));
    }

    private String updateTable(Map<String, List<String>> params) throws ResponseException {
        if (notContains(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        if (notContains(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final String tableName = getStringValue(params, HtmlParams.NAME);
        final UpdatingDatabase fields = deserialize(
                getStringValue(params, HtmlParams.DATA),
                UpdatingDatabase.class
        );

        for (int i = 0; i < fields.newValues.size(); i++) {
            fields.oldValues.set(i, fromBase64(fields.oldValues.get(i)));
            fields.newValues.set(i, fromBase64(fields.newValues.get(i)));
        }

        getDBAccess().updateData(tableName, fields.headers, fields.oldValues, fields.newValues);
        return EMPTY;
    }

    private String deleteTableItems(Map<String, List<String>> params) throws ResponseException {
        if (notContains(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        if (notContains(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final String tableName = getStringValue(params, HtmlParams.NAME);
        final DeletingDatabase deletingDatabase = deserialize(
                getStringValue(params, HtmlParams.DATA),
                DeletingDatabase.class
        );

        for (int i = 0; i < deletingDatabase.fields.size(); i++) {
            for (int j = 0; j < deletingDatabase.fields.get(i).size(); j++) {
                deletingDatabase.fields.get(i).set(j, fromBase64(deletingDatabase.fields.get(i).get(j)));
            }
        }

        getDBAccess().removeItems(tableName, deletingDatabase.headers, deletingDatabase.fields);
        return EMPTY;
    }

    private String dropTable(Map<String, List<String>> params) throws NanoHTTPD.ResponseException {
        if (notContains(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final String tableName = getStringValue(params, HtmlParams.NAME);
        getDBAccess().dropTable(tableName);
        return EMPTY;
    }

    private String dropDatabase(Map<String, List<String>> params) throws ResponseException {
        if (notContains(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final String databaseName = getStringValue(params, HtmlParams.NAME);
        getDBAccess().dropDatabase(databaseName);
        return EMPTY;
    }

    private String getDatabases() {
        List<String> databases = DatabaseManager.getDBNameList(context);
        Collections.sort(databases, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        return serialize(databases);
    }

    private String getByQuery(Map<String, List<String>> params) throws ResponseException {
        if (notContains(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        final String query = getStringValue(params, HtmlParams.DATA);
        return serialize(getDBAccess().getTableDataByQuery(query));
    }

    private String search(Map<String, List<String>> params) throws ResponseException {
        if (notContains(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        if (notContains(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final String tableName = getStringValue(params, HtmlParams.NAME);
        final String searchText = getStringValue(params, HtmlParams.DATA);

        return serialize(getDBAccess().search(tableName, searchText));
    }

    private DatabaseManager getDBAccess() {
        return DatabaseManager.getInstance();
    }
}
