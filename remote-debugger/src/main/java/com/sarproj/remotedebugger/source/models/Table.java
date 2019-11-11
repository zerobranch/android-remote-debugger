package com.sarproj.remotedebugger.source.models;

import java.util.List;

public class Table {
    public List<Header> headers;
    public List<List<String>> data;
    public int count;

    public static class Header {
        public String name;
        public String type;
        public boolean isMutable;
    }
}
