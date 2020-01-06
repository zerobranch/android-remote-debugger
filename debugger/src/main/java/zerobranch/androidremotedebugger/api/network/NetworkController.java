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
package zerobranch.androidremotedebugger.api.network;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import zerobranch.androidremotedebugger.api.base.Controller;
import zerobranch.androidremotedebugger.http.Host;
import zerobranch.androidremotedebugger.settings.InternalSettings;
import zerobranch.androidremotedebugger.source.local.StatusCodeFilter;
import zerobranch.androidremotedebugger.source.managers.ContinuousDBManager;
import zerobranch.androidremotedebugger.source.models.httplog.HttpLogModel;
import zerobranch.androidremotedebugger.utils.FileUtils;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static zerobranch.androidremotedebugger.source.local.Constants.LIMIT_HTTP_LOGS_PACKS;

public final class NetworkController extends Controller {
    private static final int UNLIMITED_OFFSET = -1;

    public NetworkController(Context context, InternalSettings internalSettings) {
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