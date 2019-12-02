package com.zerobranch.androidremotedebugger.api.base;

import android.content.Context;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.zerobranch.androidremotedebugger.settings.InternalSettings;
import com.zerobranch.androidremotedebugger.utils.InternalUtils;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

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
        return prettyPrintJson.toJson(new JsonParser().parse(item));
    }

    protected String fromBase64(String value) {
        return new String(Base64.decode(value, Base64.DEFAULT), StandardCharsets.UTF_8);
    }
}
