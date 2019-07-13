package com.sarproj.remotedebugger.source.models;

import java.util.Map;

public class HttpLogModel {
    public long id;
    public String method;
    public int code = -1;
    public String message;
    public long requestStartTime = -1;
    public long requestDuration = -1;
    public String requestContentType;
    public long requestBodySize = -1;
    public long responseBodySize = -1;
    public String baseUrl;
    public int port = -1;
    public String ip;
    public String fullUrl;
    public String shortUrl;
    public String requestBody;
    public String errorMessage;
    public String responseBody;
    public boolean isCompletedRequest;
    public Map<String, String> requestHeaders;
    public Map<String, String> responseHeaders;
    public Map<String, String> queryParams;
}