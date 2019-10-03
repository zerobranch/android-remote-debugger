package com.sarproj.remotedebugger.api.base;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sarproj.remotedebugger.settings.InternalSettings;
import com.sarproj.remotedebugger.source.local.Theme;
import com.sarproj.remotedebugger.utils.InternalUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public abstract class Api {
    protected static final String EMPTY = "";
    protected static final int DEFAULT_FONT_SIZE = 12;
    protected static final Theme DEFAULT_THEME = Theme.DARK;
    protected Context context;
    private Gson gson;
    private Gson prettyPrintJson;
    protected InternalSettings internalSettings;

    public Api(Context context, InternalSettings internalSettings) {
        this.context = context;
        this.internalSettings = internalSettings;
        gson = new Gson();
        prettyPrintJson = new GsonBuilder().setPrettyPrinting().create();
    }

    public abstract String execute(Map<String, List<String>> params) throws NanoHTTPD.ResponseException;

    @SuppressWarnings("ConstantConditions")
    protected boolean containsValue(Map<String, List<String>> params, String key) {
        return params.containsKey(key) && params.get(key) != null && !params.get(key).isEmpty();
    }

    @SuppressWarnings({"ConstantConditions", "SameParameterValue", "WeakerAccess"})
    protected String getStringValue(Map<String, List<String>> params, String key, String defaultValue) {
        if (!containsValue(params, key)) {
            return defaultValue;
        }
        return params.get(key).get(0);
    }

    protected String getStringValue(Map<String, List<String>> params, String key) {
        return getStringValue(params, key, null);
    }

    @SuppressWarnings("ConstantConditions")
    protected int getIntValue(Map<String, List<String>> params, String key, int defaultValue) {
        if (!containsValue(params, key)) {
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
        if (!containsValue(params, key)) {
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

    protected String prettyJson(String item) {
        return prettyPrintJson.toJson(new JsonParser().parse(item));
    }

    protected boolean isJson(String json) {
        try {
            new Gson().getAdapter(JsonElement.class).fromJson(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void destroy() {
        context = null;
        gson = null;
        prettyPrintJson = null;
    }
}
