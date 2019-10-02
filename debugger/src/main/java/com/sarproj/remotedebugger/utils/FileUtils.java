package com.sarproj.remotedebugger.utils;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class FileUtils {
    private static final char[] SQLITE_HEADER = new char[]{'S', 'Q', 'L', 'i', 't', 'e'};
    private static final String JOURNAL_FILE_SUFFIX = "-journal";

    public static File searchFile(File rootFolder, String fileName) {
        for (File fileEntry : rootFolder.listFiles()) {
            if (fileEntry.isDirectory()) {
                File file = searchFile(fileEntry, fileName);
                if (file != null) {
                    return file;
                }
            } else {
                if (fileEntry.getName().equalsIgnoreCase(fileName)) {
                    return fileEntry;
                }
            }
        }
        return null;
    }

    public static List<String> getDBFilesNames(File folder) {
        final List<String> files = new ArrayList<>();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.getAbsolutePath().endsWith(JOURNAL_FILE_SUFFIX)) {
                continue;
            }

            if (fileEntry.isDirectory()) {
                files.addAll(getDBFilesNames(fileEntry));
            } else {
                if (isSqliteFile(fileEntry)) {
                    files.add(fileEntry.getName());
                }
            }
        }
        return files;
    }

    public static List<String> getFilesNamesByExtension(File folder, String fileExtension) {
        final List<String> files = new ArrayList<>();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                files.addAll(getFilesNamesByExtension(fileEntry, fileExtension));
            } else {
                if (fileEntry.getAbsolutePath().toLowerCase().endsWith(fileExtension)) {
                    files.add(fileEntry.getName().replace(fileExtension, ""));
                }
            }
        }
        return files;
    }

    public static String getFileNameWithoutExt(String name) {
        return name.replaceFirst("[.][^.]+$", "");
    }

    public static String getTextFromAssets(AssetManager assetManager, String name) {
        try (InputStream is = assetManager.open(name);
             BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder buf = new StringBuilder();
            String str;

            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    buf.append('\n');
                }

                buf.append(str);
            }

            return buf.toString();
        } catch (IOException ex) {
            return ex.toString();
        }
    }

    public static InputStream getStreamFromAssets(AssetManager assetManager, String name) throws IOException {
        return assetManager.open(name);
    }

    public static void deleteFile(File file) {
        file.delete();
    }

    private static boolean isSqliteFile(File filename) {
        try (FileInputStream ins = new FileInputStream(filename)) {
            for (char c : SQLITE_HEADER) {
                if (ins.read() != c) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}