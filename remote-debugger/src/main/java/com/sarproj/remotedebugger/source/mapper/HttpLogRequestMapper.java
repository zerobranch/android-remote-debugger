package com.sarproj.remotedebugger.source.mapper;

import com.sarproj.remotedebugger.source.local.Constants;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogModel;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogRequest;
import com.sarproj.remotedebugger.source.models.httplog.QueryType;

import java.util.ArrayList;
import java.util.Map;

public class HttpLogRequestMapper {

    public HttpLogModel map(HttpLogRequest request) {
        HttpLogModel httpLogModel = new HttpLogModel();
        httpLogModel.queryId = "id: " + request.queryId;
        httpLogModel.method = request.method;
        httpLogModel.time = Constants.defaultDateFormat.format(request.time);
        httpLogModel.requestContentType = request.requestContentType;
        httpLogModel.bodySize = request.bodySize == null ? null : request.bodySize + " byte";
        httpLogModel.port = request.port;
        httpLogModel.ip = request.ip;
        httpLogModel.fullIpAddress = request.ip == null ? null : request.ip + ":" + request.port;
        httpLogModel.url = request.url;
        httpLogModel.body = request.body;
        httpLogModel.queryType = QueryType.REQUEST;

        httpLogModel.headers = new ArrayList<>();
        if (request.headers != null) {
            for (Map.Entry<String, String> header : request.headers.entrySet()) {
                httpLogModel.headers.add(header.getKey() + ": " + header.getValue());
            }
        }
        return httpLogModel;
    }
}
