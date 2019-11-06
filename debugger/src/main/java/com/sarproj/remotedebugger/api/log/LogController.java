package com.sarproj.remotedebugger.api.log;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.sarproj.remotedebugger.api.base.Controller;
import com.sarproj.remotedebugger.http.Host;
import com.sarproj.remotedebugger.settings.InternalSettings;
import com.sarproj.remotedebugger.source.local.LogLevel;
import com.sarproj.remotedebugger.source.managers.ContinuousDBManager;
import com.sarproj.remotedebugger.source.models.LogModel;
import com.sarproj.remotedebugger.utils.FileUtils;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static com.sarproj.remotedebugger.source.local.Constants.LIMIT_LOGS_PACKS;

public final class LogController extends Controller {
    private static final int UNLIMITED_OFFSET = -1;

    public LogController(Context context, InternalSettings internalSettings) {
        super(context, internalSettings);
    }

    @Override
    public String execute(Map<String, List<String>> params) throws NanoHTTPD.ResponseException {
        if (params == null || params.isEmpty()) {
            return FileUtils.getTextFromAssets(context.getAssets(), Host.LOGGING.getPath());
        } else if (params.containsKey(LogHtmlKey.GET_LOGS)) {
            return getLogs(params);
        } else if (params.containsKey(LogHtmlKey.CLEAR_ALL_LOGS)) {
            return clearAllLogs();
        }

        return EMPTY;
    }

    private String clearAllLogs() {
        getDataBase().clearAllLogs();
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

        List<LogModel> logs = getDataBase().getLogsByFilter(
                offset,
                LIMIT_LOGS_PACKS,
                primaryLevel,
                primaryTag,
                primarySearch
        );

        if (internalSettings.isEnabledJsonPrettyPrint()) {
            for (LogModel log : logs) {
                if (TextUtils.isEmpty(log.message)) {
                    continue;
                }

                try {
                    log.message = prettyJson(log.message);
                } catch (JsonSyntaxException ignored) { }
            }
        }

        return serialize(logs);
    }

    private ContinuousDBManager getDataBase() {
        return ContinuousDBManager.getInstance();
    }
}
