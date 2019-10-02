package com.sarproj.remotedebugger.source.models.httplog;

import java.util.List;

public class HttpLogModel {
    public long id;
    public String time;
    public String queryId;
    public String url;
    public String method;
    public Integer code;
    public String fullStatus;
    public String message;
    public String port;
    public String ip;
    public String fullIpAddress;
    public String requestContentType;
    public String duration;
    public String bodySize;
    public String body;
    public String errorMessage;
    public QueryType queryType;
    public List<String> headers;
}