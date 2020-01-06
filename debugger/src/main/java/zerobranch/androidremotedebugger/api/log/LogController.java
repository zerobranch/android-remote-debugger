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
package zerobranch.androidremotedebugger.api.log;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import zerobranch.androidremotedebugger.api.base.Controller;
import zerobranch.androidremotedebugger.http.Host;
import zerobranch.androidremotedebugger.settings.InternalSettings;
import zerobranch.androidremotedebugger.source.local.LogLevel;
import zerobranch.androidremotedebugger.source.managers.ContinuousDBManager;
import zerobranch.androidremotedebugger.source.models.LogModel;
import zerobranch.androidremotedebugger.utils.FileUtils;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static zerobranch.androidremotedebugger.source.local.Constants.LIMIT_LOGS_PACKS;

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
