package com.zerobranch.androidremotedebugger.source.models;

import java.util.List;

public class Tables {
    public final List<String> tables;
    public final int databaseVersion;

    public Tables(List<String> tables, int databaseVersion) {
        this.tables = tables;
        this.databaseVersion = databaseVersion;
    }
}
