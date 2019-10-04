package com.sarproj.remotedebugger.api.log;

import android.content.Context;

import com.sarproj.remotedebugger.api.base.Api;
import com.sarproj.remotedebugger.api.base.HtmlParams;
import com.sarproj.remotedebugger.settings.InternalSettings;
import com.sarproj.remotedebugger.settings.SettingsPrefs;
import com.sarproj.remotedebugger.source.managers.ContinuousDBManager;
import com.sarproj.remotedebugger.source.local.LogLevel;
import com.sarproj.remotedebugger.source.models.DefaultSettings;
import com.sarproj.remotedebugger.source.local.Theme;
import com.sarproj.remotedebugger.http.Host;
import com.sarproj.remotedebugger.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static com.sarproj.remotedebugger.source.local.Constants.LIMIT_LOGS_PACKS;

public final class LogApi extends Api {
    private static final int UNLIMITED_OFFSET = -1;
    private static final boolean DEFAULT_LOG_IS_DISCOLOR = false;

    public LogApi(Context context, InternalSettings internalSettings) {
        super(context, internalSettings);
    }

    @Override
    public String execute(Map<String, List<String>> params) throws NanoHTTPD.ResponseException {
        if (params == null || params.isEmpty()) {
            return FileUtils.getTextFromAssets(context.getAssets(), Host.LOGGING.getPath());
        } else if (params.containsKey(LogHtmlKey.GET_LOG_LEVELS)) {
            return getLogLevels();
        } else if (params.containsKey(LogHtmlKey.GET_LOGS)) {
            return getLogs(params);
        } else if (params.containsKey(LogHtmlKey.CLEAR_ALL_LOGS)) {
            return clearAllLogs();
        } else if (params.containsKey(LogHtmlKey.GET_DEFAULT_SETTINGS)) {
            return getDefaultSettings();
        } else if (containsValue(params, LogHtmlKey.SAVE_DEFAULTS_SETTING)) {
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

        SettingsPrefs.Key.THEME.save(settings.theme);
        SettingsPrefs.Key.LOG_FONT.save(settings.logFont);
        SettingsPrefs.Key.LOG_IS_DISCOLOR.save(settings.isDiscolorLog);
        return EMPTY;
    }

    private String getDefaultSettings() {
        final DefaultSettings settings = new DefaultSettings();
        settings.logFont = SettingsPrefs.Key.LOG_FONT.get(DEFAULT_FONT_SIZE);
        settings.isDiscolorLog = SettingsPrefs.Key.LOG_IS_DISCOLOR.get(DEFAULT_LOG_IS_DISCOLOR);
        settings.theme = SettingsPrefs.Key.THEME.get(DEFAULT_THEME.name());
        return serialize(settings);
    }

    private String clearAllLogs() {
        getDataBase().clearAllLog();
        return EMPTY;
    }

    private String getLogs(Map<String, List<String>> params) {
        int offset = getIntValue(params, LogHtmlKey.LOGS_OFFSET, UNLIMITED_OFFSET);
        String primaryTag = getStringValue(params, LogHtmlKey.LOGS_TAG);
        String primaryLevel = getStringValue(params, LogHtmlKey.LOGS_LEVEL);
        String primarySearch = getStringValue(params, LogHtmlKey.LOGS_SEARCH);

        if (LogLevel.VERBOSE.name().equalsIgnoreCase(primaryLevel)) {
            primaryLevel = null;
        }

        return serialize(getDataBase().getLogByFilter(offset, LIMIT_LOGS_PACKS, primaryLevel, primaryTag, primarySearch));
    }

    private ContinuousDBManager getDataBase() {
        return ContinuousDBManager.getInstance();
    }

    private String getLogLevels() {
        return serialize(new ArrayList<>(Arrays.asList(LogLevel.values())));
    }
}
