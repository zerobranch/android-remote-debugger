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
