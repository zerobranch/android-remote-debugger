package com.sarproj.remotedebugger.api.network;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.sarproj.remotedebugger.api.base.Api;
import com.sarproj.remotedebugger.http.Host;
import com.sarproj.remotedebugger.settings.InternalSettings;
import com.sarproj.remotedebugger.source.local.StatusCodeFilter;
import com.sarproj.remotedebugger.source.managers.ContinuousDBManager;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogModel;
import com.sarproj.remotedebugger.utils.FileUtils;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static com.sarproj.remotedebugger.source.local.Constants.LIMIT_HTTP_LOGS_PACKS;

public final class NetworkApi extends Api {
    private static final int UNLIMITED_OFFSET = -1;

    public NetworkApi(Context context, InternalSettings internalSettings) {
        super(context, internalSettings);
    }

    @Override
    public String execute(Map<String, List<String>> params) throws NanoHTTPD.ResponseException {
        if (params == null || params.isEmpty()) {
            return FileUtils.getTextFromAssets(context.getAssets(), Host.NETWORK.getPath());
        } else if (params.containsKey(NetworkHtmlKey.GET_LOGS)) {
            return getLogs(params);
        } else if (params.containsKey(NetworkHtmlKey.CLEAR_ALL_LOGS)) {
            return clearAllLogs();
        }

        return EMPTY;
    }

    private String clearAllLogs() {
        getDataBase().clearAllHttpLogs();
        return EMPTY;
    }

    private String getLogs(Map<String, List<String>> params) {
        int offset = getIntValue(params, NetworkHtmlKey.OFFSET, UNLIMITED_OFFSET);
        String statusCode = getStringValue(params, NetworkHtmlKey.STATUS_CODE);
        boolean isOnlyErrors = getBooleanValue(params, NetworkHtmlKey.IS_ONLY_ERRORS, false);
        String search = getStringValue(params, NetworkHtmlKey.SEARCH);

        List<HttpLogModel> logs = getDataBase().getHttpLogs(offset, LIMIT_HTTP_LOGS_PACKS,
                new StatusCodeFilter(statusCode), isOnlyErrors, search);

        if (internalSettings.isEnabledJsonPrettyPrint()) {
            for (HttpLogModel log : logs) {
                if (TextUtils.isEmpty(log.body)) {
                    continue;
                }

                try {
                    log.body = prettyJson(log.body);
                } catch (JsonSyntaxException ignored) { }
            }
        }

        return serialize(logs);
    }

    private ContinuousDBManager getDataBase() {
        return ContinuousDBManager.getInstance();
    }
}