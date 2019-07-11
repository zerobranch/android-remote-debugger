package com.sarproj.remotedebugger.source.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.sarproj.remotedebugger.utils.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SharedPrefsManager {
    private static final Object LOCK = new Object();
    private static final String SHARE_PREFS_DIR = "shared_prefs";
    private static final String XML_FILE_SUFFIX = ".xml";
    private SharedPreferences sharedPreferences;
    private static SharedPrefsManager instance;
    private final String sharedPrefsRootDir;

    private SharedPrefsManager(Context context, String prefsName) {
        sharedPrefsRootDir = new File(context.getApplicationInfo().dataDir, SHARE_PREFS_DIR).getAbsolutePath();

        sharedPreferences = context.getSharedPreferences(
                FileUtils.getFileNameWithoutExt(prefsName), Context.MODE_PRIVATE);
    }

    public static SharedPrefsManager getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException("The SharedPreferences is not open. " +
                        "Please, use '" + SharedPrefsManager.class.getName() + ".connect(Context, String)'");
            }
            return instance;
        }
    }

    public static void connect(Context context, String prefsName) {
        synchronized (LOCK) {
            disconnect();
            instance = new SharedPrefsManager(context, prefsName.concat(XML_FILE_SUFFIX));
        }
    }

    public static void disconnect() {
        synchronized (LOCK) {
            if (instance != null) {
                instance.sharedPreferences = null;
                instance = null;
            }
        }
    }

    public static List<String> getSharedPreferences(Context context) {
        synchronized (LOCK) {
            final File prefsDir = new File(context.getApplicationInfo().dataDir, SHARE_PREFS_DIR);

            if (!prefsDir.exists() || !prefsDir.isDirectory()) {
                return Collections.emptyList();
            }

            return FileUtils.getFilesNamesByExtension(prefsDir, XML_FILE_SUFFIX);
        }
    }

    public void dropSharedPreferences(String name) {
        synchronized (LOCK) {
            if (name == null) {
                return;
            }

            FileUtils.deleteFile(new File(sharedPrefsRootDir, name.concat(XML_FILE_SUFFIX)));
            disconnect();
        }
    }

    public Map<String, ?> getAllData() {
        synchronized (LOCK) {
            return sharedPreferences.getAll();
        }
    }

    public <T> void put(String key, T value) {
        synchronized (LOCK) {
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Set) {
                editor.putStringSet(key, (Set<String>) value);
            }
            editor.apply();
        }
    }

    public void removeItems(List<String> keys) {
        synchronized (LOCK) {
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            for  (String key: keys) {
                editor.remove(key);
            }
            editor.apply();
        }
    }
}
