package com.sarproj.remotedebugger.api.network;

import android.content.Context;

import com.sarproj.remotedebugger.api.base.Api;
import com.sarproj.remotedebugger.api.base.HtmlParams;
import com.sarproj.remotedebugger.http.Host;
import com.sarproj.remotedebugger.settings.Settings;
import com.sarproj.remotedebugger.source.local.StatusCodeFilter;
import com.sarproj.remotedebugger.source.local.Theme;
import com.sarproj.remotedebugger.source.managers.ContinuousDataBaseManager;
import com.sarproj.remotedebugger.source.models.DefaultSettings;
import com.sarproj.remotedebugger.utils.FileUtils;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public final class NetworkApi extends Api {
    private static final boolean DEFAULT_LOG_IS_DISCOLOR = false;
    private static final int UNLIMITED_OFFSET = -1;
    private static final int LIMIT_LOGS_PACKS = 1000;

    public NetworkApi(Context context) {
        super(context);
    }

    @Override
    public String execute(Map<String, List<String>> params) throws NanoHTTPD.ResponseException {
        if (params == null || params.isEmpty()) {
            return FileUtils.getTextFromAssets(context.getAssets(), Host.NETWORK.getPath());
        } else if (params.containsKey(NetworkHtmlKey.GET_LOGS)) {
            return getLogs(params);
        } else if (params.containsKey(NetworkHtmlKey.CLEAR_ALL_LOGS)) {
            return clearAllLogs();
        } else if (params.containsKey(NetworkHtmlKey.GET_DEFAULT_SETTINGS)) {
            return getDefaultSettings();
        } else if (containsValue(params, NetworkHtmlKey.SAVE_DEFAULTS_SETTING)) {
            return saveDefaultSettings(params);
        }

        return EMPTY;
    }

    private String saveDefaultSettings(Map<String, List<String>> params) throws NanoHTTPD.ResponseException {
        if (!containsValue(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        final String settingsJson = getStringValue(params, HtmlParams.DATA);
        final DefaultSettings settings = deserialize(settingsJson, DefaultSettings.class);

        if (settings.isDiscolorLog == null) {
            settings.isDiscolorLog = DEFAULT_LOG_IS_DISCOLOR;
        }

        if (settings.logFont == null) {
            settings.logFont = DEFAULT_FONT_SIZE;
        }

        if (Theme.notContains(settings.theme)) {
            settings.theme = DEFAULT_THEME.name();
        }

        Settings.Key.THEME.save(settings.theme);
        Settings.Key.NETWORK_FONT.save(settings.logFont);
        Settings.Key.NETWORK_LOG_IS_DISCOLOR.save(settings.isDiscolorLog);
        return EMPTY;
    }

    private String getDefaultSettings() {
        final DefaultSettings settings = new DefaultSettings();
        settings.logFont = Settings.Key.NETWORK_FONT.get(DEFAULT_FONT_SIZE);
        settings.isDiscolorLog = Settings.Key.NETWORK_LOG_IS_DISCOLOR.get(DEFAULT_LOG_IS_DISCOLOR);
        settings.theme = Settings.Key.THEME.get(DEFAULT_THEME.name());
        return serialize(settings);
    }

    private String clearAllLogs() {
        getDataBase().clearAllHttpLog();
        return EMPTY;
    }

    private String getLogs(Map<String, List<String>> params) {
        int offset = getIntValue(params, NetworkHtmlKey.OFFSET, UNLIMITED_OFFSET);
        String statusCode = getStringValue(params, NetworkHtmlKey.STATUS_CODE);
        boolean isOnlyErrors = getBooleanValue(params, NetworkHtmlKey.IS_ONLY_ERRORS, false);
        String search = getStringValue(params, NetworkHtmlKey.SEARCH);

        return serialize(getDataBase().getHttpLogs(offset, LIMIT_LOGS_PACKS, new StatusCodeFilter(statusCode), isOnlyErrors, search));
    }

    private ContinuousDataBaseManager getDataBase() {
        return ContinuousDataBaseManager.getInstance();
    }
}