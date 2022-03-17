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
package zerobranch.androidremotedebugger.api.base;

import android.content.Context;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import zerobranch.androidremotedebugger.settings.InternalSettings;
import zerobranch.androidremotedebugger.utils.InternalUtils;

public abstract class Controller {
    protected static final String EMPTY = "";
    private final Gson gson;
    private final Gson prettyPrintJson;
    protected final InternalSettings internalSettings;
    protected final Context context;

    public Controller(Context context, InternalSettings internalSettings) {
        this.context = context;
        this.internalSettings = internalSettings;
        gson = new Gson();
        prettyPrintJson = new GsonBuilder().setPrettyPrinting().create();
    }

    public abstract String execute(Map<String, List<String>> params) throws NanoHTTPD.ResponseException;

    @SuppressWarnings("ConstantConditions")
    protected boolean notContains(Map<String, List<String>> params, String key) {
        return !params.containsKey(key) || params.get(key) == null || params.get(key).isEmpty();
    }

    @SuppressWarnings("ConstantConditions")
    protected String getStringValue(Map<String, List<String>> params, String key) {
        if (notContains(params, key)) {
            return null;
        }
        return params.get(key).get(0);
    }

    @SuppressWarnings("ConstantConditions")
    protected int getIntValue(Map<String, List<String>> params, String key, int defaultValue) {
        if (notContains(params, key)) {
            return defaultValue;
        }

        String rawValue = params.get(key).get(0);
        if (!InternalUtils.isInt(rawValue)) {
            return defaultValue;
        }

        return Integer.parseInt(rawValue);
    }

    @SuppressWarnings({"ConstantConditions", "SameParameterValue"})
    protected boolean getBooleanValue(Map<String, List<String>> params, String key, boolean defaultValue) {
        if (notContains(params, key)) {
            return defaultValue;
        }

        String rawValue = params.get(key).get(0);
        if (rawValue.equalsIgnoreCase("true")) {
            return true;
        } else if (rawValue.equalsIgnoreCase("false")) {
            return false;
        } else {
            return defaultValue;
        }
    }

    protected String serialize(Object object) {
        return gson.toJson(object);
    }

    protected <T> T deserialize(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    protected <T> List<T> deserialize(String json, Type type) {
        return new Gson().fromJson(json, type);
    }

    protected void throwEmptyParameterException(String parameter) throws NanoHTTPD.ResponseException {
        throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST,
            "'" + parameter + "' parameter not found");
    }

    protected String prettyJson(String item) throws JsonSyntaxException {
        return prettyPrintJson.toJson(JsonParser.parseString(item));
    }

    protected String fromBase64(String value) {
        return new String(Base64.decode(value, Base64.DEFAULT), StandardCharsets.UTF_8);
    }
}
