package com.zerobranch.androidremotedebugger.source.local;

import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Constants {
    SimpleDateFormat defaultDateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH);
    int LIMIT_HTTP_LOGS_PACKS = 500;
    int LIMIT_LOGS_PACKS = 1000;
}
