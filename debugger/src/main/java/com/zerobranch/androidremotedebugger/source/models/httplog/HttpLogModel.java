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
package com.zerobranch.androidremotedebugger.source.models.httplog;

import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public String toString() {
        return "HttpLogModel{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", queryId='" + queryId + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", code=" + code +
                ", fullStatus='" + fullStatus + '\'' +
                ", message='" + message + '\'' +
                ", port='" + port + '\'' +
                ", ip='" + ip + '\'' +
                ", fullIpAddress='" + fullIpAddress + '\'' +
                ", requestContentType='" + requestContentType + '\'' +
                ", duration='" + duration + '\'' +
                ", bodySize='" + bodySize + '\'' +
                ", body='" + body + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", queryType=" + queryType +
                ", headers=" + headers +
                '}';
    }
}