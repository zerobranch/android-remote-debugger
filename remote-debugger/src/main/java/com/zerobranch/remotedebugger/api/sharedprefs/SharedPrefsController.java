package com.zerobranch.remotedebugger.api.sharedprefs;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.zerobranch.remotedebugger.api.base.Controller;
import com.zerobranch.remotedebugger.api.base.HtmlParams;
import com.zerobranch.remotedebugger.http.Host;
import com.zerobranch.remotedebugger.settings.InternalSettings;
import com.zerobranch.remotedebugger.source.managers.SharedPrefsManager;
import com.zerobranch.remotedebugger.source.models.SharedPrefsData;
import com.zerobranch.remotedebugger.utils.FileUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.iki.elonen.NanoHTTPD.ResponseException;

public class SharedPrefsController extends Controller {
    private final static String TYPE_INTEGER = "Integer";
    private final static String TYPE_FLOAT = "Float";
    private final static String TYPE_LONG = "Long";
    private final static String TYPE_STRING = "String";
    private final static String TYPE_BOOLEAN = "Boolean";
    private final static String TYPE_SET_STRING = "Set<String>";

    public SharedPrefsController(Context context, InternalSettings internalSettings) {
        super(context, internalSettings);
    }

    @Override
    public String execute(Map<String, List<String>> params) throws ResponseException {
        if (params == null || params.isEmpty()) {
            return FileUtils.getTextFromAssets(context.getAssets(), Host.SHARED_REFERENCES.getPath());
        } else if (params.containsKey(SharedPrefsKey.GET_ALL_NAMES)) {
            return getAllSharedPreferencesNames();
        } else if (params.containsKey(SharedPrefsKey.GET_ALL)) {
            return getAll(params);
        } else if (params.containsKey(SharedPrefsKey.DROP)) {
            return dropSharedPreferences(params);
        } else if (params.containsKey(SharedPrefsKey.UPDATE)) {
            return update(params);
        } else if (params.containsKey(SharedPrefsKey.REMOVE)) {
            return remove(params);
        }

        return EMPTY;
    }

    private String remove(Map<String, List<String>> params) throws ResponseException {
        if (notContains(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        final String data = getStringValue(params, HtmlParams.DATA);
        final List<String> keys = deserialize(data, new TypeToken<List<String>>(){}.getType());
        getSharedPrefsAccess().removeItems(keys);

        return EMPTY;
    }

    private String update(Map<String, List<String>> params) throws ResponseException {
        if (notContains(params, HtmlParams.DATA)) {
            throwEmptyParameterException(HtmlParams.DATA);
        }

        final String data = getStringValue(params, HtmlParams.DATA);
        final SharedPrefsData prefsData = deserialize(data, SharedPrefsData.class);
        final SharedPrefsManager manager = getSharedPrefsAccess();

        if (prefsData.type.equalsIgnoreCase(TYPE_INTEGER)) {
            throwIfNotInteger(prefsData.value);

            manager.put(prefsData.key, Integer.parseInt(prefsData.value));
        } else if (prefsData.type.equalsIgnoreCase(TYPE_FLOAT)) {
            manager.put(prefsData.key, Float.parseFloat(prefsData.value));
        } else if (prefsData.type.equalsIgnoreCase(TYPE_BOOLEAN)) {
            manager.put(prefsData.key, Boolean.parseBoolean(prefsData.value));
        } else if (prefsData.type.equalsIgnoreCase(TYPE_LONG)) {
            throwIfNotLong(prefsData.value);

            manager.put(prefsData.key, Long.parseLong(prefsData.value));
        } else if (prefsData.type.equalsIgnoreCase(TYPE_STRING)) {
            manager.put(prefsData.key, prefsData.value);
        } else if (prefsData.type.equalsIgnoreCase(TYPE_SET_STRING)) {
            final String[] splitData = prefsData.value
                    .replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .split(",");
            manager.put(prefsData.key, new HashSet<>(Arrays.asList(splitData)));
        }

        return EMPTY;
    }

    private String dropSharedPreferences(Map<String, List<String>> params) throws ResponseException {
        if (notContains(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final String name = getStringValue(params, HtmlParams.NAME);
        getSharedPrefsAccess().dropSharedPreferences(name);
        return EMPTY;
    }

    private String getAllSharedPreferencesNames() {
        List<String> sharedPreferences = SharedPrefsManager.getSharedPreferences(context);
        Collections.sort(sharedPreferences, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        return serialize(sharedPreferences);
    }

    private String getAll(Map<String, List<String>> params) throws ResponseException {
        if (notContains(params, HtmlParams.NAME)) {
            throwEmptyParameterException(HtmlParams.NAME);
        }

        final List<SharedPrefsData> sharedPrefsDataList = new ArrayList<>();
        final String name = getStringValue(params, HtmlParams.NAME);
        SharedPrefsManager.connect(context, name);
        final Map<String, ?> allData = getSharedPrefsAccess().getAllData();

        for (Map.Entry<String, ?> entry : allData.entrySet()) {
            final Object value = entry.getValue();

            final SharedPrefsData sharedPrefsData = new SharedPrefsData();
            sharedPrefsData.key = entry.getKey();
            sharedPrefsData.value = String.valueOf(value);

            if (value instanceof Integer) {
                sharedPrefsData.type = TYPE_INTEGER;
            } else if (value instanceof Float) {
                sharedPrefsData.type = TYPE_FLOAT;
            } else if (value instanceof Long) {
                sharedPrefsData.type = TYPE_LONG;
            } else if (value instanceof String) {
                sharedPrefsData.type = TYPE_STRING;
            } else if (value instanceof Boolean) {
                sharedPrefsData.type = TYPE_BOOLEAN;
            } else if (value instanceof Set) {
                sharedPrefsData.type = TYPE_SET_STRING;
                sharedPrefsData.value = sharedPrefsData.value.replaceAll(", ", ",");
            }

            sharedPrefsDataList.add(sharedPrefsData);
        }

        return serialize(sharedPrefsDataList);
    }

    private SharedPrefsManager getSharedPrefsAccess() {
        return SharedPrefsManager.getInstance();
    }

    private void throwIfNotLong(String data) {
        final BigDecimal inVal = new BigDecimal(data);
        final BigDecimal maxLong = new BigDecimal(Long.MAX_VALUE);
        final BigDecimal minLong = new BigDecimal(Long.MIN_VALUE);

        if (inVal.compareTo(maxLong) > 0 && inVal.compareTo(minLong) < 0) {
            throw new RuntimeException("Long number too large");
        }
    }

    private void throwIfNotInteger(String data) {
        final BigDecimal inVal = new BigDecimal(data);
        final BigDecimal maxInt = new BigDecimal(Integer.MAX_VALUE);
        final BigDecimal minInt = new BigDecimal(Integer.MIN_VALUE);

        if (inVal.compareTo(maxInt) > 0 && inVal.compareTo(minInt) < 0) {
            throw new RuntimeException("Integer number too large");
        }
    }
}
