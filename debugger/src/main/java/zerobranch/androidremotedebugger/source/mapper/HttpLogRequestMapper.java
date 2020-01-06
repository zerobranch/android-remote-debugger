/*
 * Copyright 2020 Arman Sargsyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zerobranch.androidremotedebugger.source.mapper;

import zerobranch.androidremotedebugger.source.local.Constants;
import zerobranch.androidremotedebugger.source.models.httplog.HttpLogModel;
import zerobranch.androidremotedebugger.source.models.httplog.HttpLogRequest;
import zerobranch.androidremotedebugger.source.models.httplog.QueryType;

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
