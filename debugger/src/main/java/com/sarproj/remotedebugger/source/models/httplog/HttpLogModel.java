package com.sarproj.remotedebugger.source.models.httplog;

import java.util.Map;

public class HttpLogModel {
    public long id;
    public long time;
    public String queryId;
    public String url;
    public String method;
    public String code;
    public String message;
    public String port;
    public String ip;
    public String requestContentType;
    public String duration;
    public String bodySize;
    public String body;
    public String errorMessage;
    public QueryType queryType;
    public Map<String, String> headers;
}