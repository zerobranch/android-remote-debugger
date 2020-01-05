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

import java.util.Map;

public class HttpLogResponse {
    public long id;
    public long time;
    public String queryId;
    public String url;
    public String port;
    public String ip;
    public String method;
    public int code = -1;
    public String message;
    public String duration;
    public String bodySize;
    public String body;
    public String errorMessage;
    public Map<String, String> headers;
}
