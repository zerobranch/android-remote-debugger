package com.sarproj.remotedebugger.settings;

import android.content.Context;
import android.content.SharedPreferences;

public final class Settings {
    private static final String SHARED_PREFERENCES_NAME = "remote_debugger_settings";
    private static Settings instance;
    private final SharedPreferences preferences;

    public static void init(Context context) {
        instance = new Settings(context.getApplicationContext());
    }

    private Settings(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private static Settings getInstance() {
        if (instance != null) {
            return instance;
        }

        throw new IllegalArgumentException("Please, use " + Settings.class.getName() +
                ".init(Context) before using this method.");
    }

    public static void destroy() {
        instance = null;
    }

    public enum Key {
        THEME(String.class),
        NETWORK_FONT(int.class),
        LOG_FONT(int.class),
        DATABASE_FONT(int.class),
        SHARED_PREFERENCES_FONT(int.class),
        LOG_IS_DISCOLOR(boolean.class);

        private Class<?> clazz;

        Key(Class<?> clazz) {
            this.clazz = clazz;
        }

        @SuppressWarnings("unchecked")
        public <T> T get(T defaultValue) {
            final SharedPreferences preferences = getInstance().preferences;

            if (clazz.getName().equals(int.class.getName()) && defaultValue instanceof Integer) {
                return (T) ((Integer) preferences.getInt(name(), (Integer) defaultValue));
            } else if (clazz.getName().equals(long.class.getName()) && defaultValue instanceof Long) {
                return (T) ((Long) preferences.getLong(name(), (Long) defaultValue));
            } else if (clazz.getName().equals(float.class.getName()) && defaultValue instanceof Float) {
                return (T) ((Float) preferences.getFloat(name(), (Float) defaultValue));
            } else if (clazz.getName().equals(boolean.class.getName()) && defaultValue instanceof Boolean) {
                return (T) ((Boolean) preferences.getBoolean(name(), (Boolean) defaultValue));
            } else if (clazz.getName().equals(String.class.getName()) && defaultValue instanceof String) {
                return (T) preferences.getString(name(), (String) defaultValue);
            }

            throw new RuntimeException("Invalid type '" + defaultValue.getClass().getName() + "'. " +
                    "Must be '" + clazz.getName() + "'");
        }

        @SuppressWarnings("unchecked")
        public <T> T get() {
            final SharedPreferences preferences = getInstance().preferences;

            if (clazz.getName().equals(int.class.getName())) {
                return (T) ((Integer) preferences.getInt(name(), 0));
            } else if (clazz.getName().equals(long.class.getName())) {
                return (T) ((Long) preferences.getLong(name(), 0));
            } else if (clazz.getName().equals(float.class.getName())) {
                return (T) ((Float) preferences.getFloat(name(), 0));
            } else if (clazz.getName().equals(boolean.class.getName())) {
                return (T) ((Boolean) preferences.getBoolean(name(), false));
            } else if (clazz.getName().equals(String.class.getName())) {
                return (T) preferences.getString(name(), null);
            }

            throw new RuntimeException("Invalid type '" + clazz.getName() + "'.");
        }

        public <T> void save(T value) {
            final SharedPreferences.Editor edit = getInstance().preferences.edit();

            if (clazz.getName().equals(int.class.getName()) && value instanceof Integer) {
                edit.putInt(name(), (Integer) value);
            } else if (clazz.getName().equals(long.class.getName()) && value instanceof Long) {
                edit.putLong(name(), (Long) value);
            } else if (clazz.getName().equals(float.class.getName()) && value instanceof Float) {
                edit.putFloat(name(), (Float) value);
            } else if (clazz.getName().equals(boolean.class.getName()) && value instanceof Boolean) {
                edit.putBoolean(name(), (Boolean) value);
            } else if (clazz.getName().equals(String.class.getName()) && value instanceof String) {
                edit.putString(name(), (String) value);
            } else {
                throw new RuntimeException("Invalid type '" + clazz.getName() + "'.");
            }

            edit.apply();
        }

        public void remove() {
            getInstance().preferences.edit().remove(name()).apply();
        }
    }
}
