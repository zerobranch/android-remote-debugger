package com.sarproj.remotedebugger.api.database;

import android.content.Context;

import com.sarproj.remotedebugger.api.base.Api;
import com.sarproj.remotedebugger.api.base.HtmlParams;
import com.sarproj.remotedebugger.http.Host;
import com.sarproj.remotedebugger.settings.InternalSettings;
import com.sarproj.remotedebugger.settings.SettingsPrefs;
import com.sarproj.remotedebugger.source.local.Theme;
import com.sarproj.remotedebugger.source.managers.DatabaseManager;
import com.sarproj.remotedebugger.source.models.DefaultSettings;
import com.sarproj.remotedebugger.source.models.DeletingDatabase;
import com.sarproj.remotedebugger.source.models.Table;
import com.sarproj.remotedebugger.source.models.Tables;
import com.sarproj.remotedebugger.source.models.UpdatingDatabase;
import com.sarproj.remotedebugger.utils.FileUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.ResponseException;

public class DatabaseApi extends Api {
    private static final int FIRST_PAGE = 1;
    private static final int LAST_PAGE = -1;
    private static final int DEFAULT_PAGE_SIZE = 15;

    public DatabaseApi(Context context, InternalSettings internalSettings) {
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
        } else if (params.containsKey(DatabaseHtmlKey.GET_DEFAULT_SETTINGS)) {
            return getDefaultSettings();
        } else if (containsValue(params, DatabaseHtmlKey.SAVE_DEFAULTS_SETTING)) {
            return saveDefaultSettings(params);
        }

        return EMPTY;
    }

    private String getTable(Map<String, List<String>> params) throws ResponseException {
        if (!containsValue(params, HtmlParams.NAME)) {
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
        if (!containsValue(params, HtmlParams.DATABASE)) {
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
        if (!containsValue(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        if (!containsValue(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final String tableName = getStringValue(params, HtmlParams.NAME);
        final UpdatingDatabase fields = deserialize(
                getStringValue(params, HtmlParams.DATA), // todo найти каждое значение old value и new value - потом перевести в base64 а потом перевести в объект с gson а потом снова в строку перевести
                UpdatingDatabase.class
        );

        getDBAccess().updateData(tableName, fields.headers, fields.oldValues, fields.newValues);
        return EMPTY;
    }

    private String deleteTableItems(Map<String, List<String>> params) throws ResponseException {
        if (!containsValue(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        if (!containsValue(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final String tableName = getStringValue(params, HtmlParams.NAME);
        final DeletingDatabase deletingDatabase = deserialize(
                getStringValue(params, HtmlParams.DATA),
                DeletingDatabase.class
        );

        getDBAccess().removeItems(tableName, deletingDatabase.headers, deletingDatabase.fields);
        return EMPTY;
    }

    private String dropTable(Map<String, List<String>> params) throws NanoHTTPD.ResponseException {
        if (!containsValue(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final String tableName = getStringValue(params, HtmlParams.NAME);
        getDBAccess().dropTable(tableName);
        return EMPTY;
    }

    private String dropDatabase(Map<String, List<String>> params) throws ResponseException {
        if (!containsValue(params, HtmlParams.NAME)) {
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
        if (!containsValue(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        final String query = getStringValue(params, HtmlParams.DATA);
        return serialize(getDBAccess().getTableDataByQuery(query));
    }

    private String search(Map<String, List<String>> params) throws ResponseException {
        if (!containsValue(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        if (!containsValue(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final String tableName = getStringValue(params, HtmlParams.NAME);
        final String searchText = getStringValue(params, HtmlParams.DATA);

        return serialize(getDBAccess().search(tableName, searchText));
    }

    private String getDefaultSettings() {
        final DefaultSettings settings = new DefaultSettings();
        settings.theme = SettingsPrefs.Key.THEME.get(DEFAULT_THEME.name());
        settings.databaseFont = SettingsPrefs.Key.DATABASE_FONT.get(DEFAULT_FONT_SIZE);
        return serialize(settings);
    }

    private String saveDefaultSettings(Map<String, List<String>> params) throws ResponseException {
        if (!containsValue(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        final String settingsJson = getStringValue(params, HtmlParams.DATA);
        final DefaultSettings settings = deserialize(settingsJson, DefaultSettings.class);

        if (settings.databaseFont == null) {
            settings.databaseFont = DEFAULT_FONT_SIZE;
        }

        if (Theme.notContains(settings.theme)) {
            settings.theme = DEFAULT_THEME.name();
        }

        SettingsPrefs.Key.THEME.save(settings.theme);
        SettingsPrefs.Key.DATABASE_FONT.save(settings.databaseFont);
        return EMPTY;
    }

    private DatabaseManager getDBAccess() {
        return DatabaseManager.getInstance();
    }
}
