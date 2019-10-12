package com.sarproj.remotedebugger.source.mapper;

import com.sarproj.remotedebugger.source.local.Constants;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogModel;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogResponse;
import com.sarproj.remotedebugger.source.models.httplog.QueryType;

import java.util.ArrayList;
import java.util.Map;

public class HttpLogResponseMapper {

    public HttpLogModel map(HttpLogResponse response) {
        HttpLogModel httpLogModel = new HttpLogModel();
        httpLogModel.queryId = "id: " + response.queryId;
        httpLogModel.method = response.method;
        httpLogModel.time = Constants.defaultDateFormat.format(response.time);
        httpLogModel.code = response.code;
        httpLogModel.message = response.message;
        httpLogModel.fullStatus = response.code == -1 ? null : response.code + " " + response.message;
        httpLogModel.duration = response.duration == null ? null : response.duration + " ms";
        httpLogModel.bodySize = response.bodySize == null ? null : response.bodySize + " byte";
        httpLogModel.port = response.port;
        httpLogModel.ip = response.ip;
        httpLogModel.fullIpAddress = response.ip == null ? null : response.ip + ":" + response.port;
        httpLogModel.url = response.url;
        httpLogModel.errorMessage = response.errorMessage;
        httpLogModel.body = response.body;
        httpLogModel.queryType = QueryType.RESPONSE;

        httpLogModel.headers = new ArrayList<>();
        if (response.headers != null) {
            for (Map.Entry<String, String> header : response.headers.entrySet()) {
                httpLogModel.headers.add(header.getKey() + ": " + header.getValue());
            }
        }
        return httpLogModel;
    }
}
