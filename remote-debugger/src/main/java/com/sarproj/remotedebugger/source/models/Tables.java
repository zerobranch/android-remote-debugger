package com.sarproj.remotedebugger.source.models;

import java.util.List;

public class Tables {
    public List<String> tables;
    public int databaseVersion;

    public Tables(List<String> tables, int databaseVersion) {
        this.tables = tables;
        this.databaseVersion = databaseVersion;
    }
}
