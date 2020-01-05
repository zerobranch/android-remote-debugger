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
package com.zerobranch.androidremotedebugger.http;

import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

public class HttpResponse extends NanoHTTPD.Response {

    public HttpResponse(IStatus status, String mimeType, InputStream data, long totalBytes) {
        super(status, mimeType, data, totalBytes);
    }

    public static NanoHTTPD.Response newCssResponse(InputStream data) {
        return new HttpResponse(NanoHTTPD.Response.Status.OK, "text/css", data, 0);
    }

    public static NanoHTTPD.Response newPngResponse(InputStream data) throws IOException {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "image/jpeg", data, data.available());
    }

    public static NanoHTTPD.Response newFixedLengthResponse(String body) {
        return NanoHTTPD.newFixedLengthResponse(body);
    }

    public static NanoHTTPD.Response newErrorResponse(Status status, String message) {
        return NanoHTTPD.newFixedLengthResponse(status, NanoHTTPD.MIME_PLAINTEXT, message);
    }
}
