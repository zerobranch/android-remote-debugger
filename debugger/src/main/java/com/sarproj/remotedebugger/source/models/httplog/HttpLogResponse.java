package com.sarproj.remotedebugger.source.models.httplog;

import java.util.Map;

public class HttpLogResponse {
    public long id;
    public String queryId;
    public String method;
    public String code;
    public String message;
    public long responseTime;
    public long requestDuration;
    public long responseBodySize;
    public String baseUrl;
    public String port;
    public String ip;
    public String fullUrl;
    public String shortUrl;
    public String errorMessage;
    public String responseBody;
    public Map<String, String> responseHeaders;
}
