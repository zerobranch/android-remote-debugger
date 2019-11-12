package com.zerobranch.remotedebugger.source.models.httplog;

import java.util.Map;

public class HttpLogRequest {
    public long id;
    public long time;
    public String queryId;
    public String url;
    public String method;
    public String ip;
    public String port;
    public String requestContentType;
    public String bodySize;
    public String body;
    public Map<String, String> headers;
}
