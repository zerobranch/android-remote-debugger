package com.sarproj.remotedebugger.logging;

import android.net.Uri;

import com.sarproj.remotedebugger.source.managers.ContinuousDataBaseManager;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogRequest;
import com.sarproj.remotedebugger.source.models.httplog.HttpLogResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

public class NetLoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Response intercept(@NotNull Chain chain) throws IOException {
        HttpLogRequest logRequest = new HttpLogRequest();
        HttpLogResponse logResponse = new HttpLogResponse();

        Request request = chain.request();
        RequestBody requestBody = request.body();

        logRequest.method = request.method();
        logRequest.fullUrl = request.url().toString();
        logRequest.baseUrl = request.url().host();

        String query = request.url().url().getQuery();
        logRequest.shortUrl = request.url().url().getPath() + (query != null ? "?" + query : "");
        logRequest.port = String.valueOf(request.url().port());

        InetAddress address = InetAddress.getByName(new URL(request.url().toString()).getHost());
        logRequest.ip = address.getHostAddress();

        logRequest.queryParams = new HashMap<>();
        Uri requestUri = Uri.parse(request.url().toString());
        Set<String> queryParamNames = requestUri.getQueryParameterNames();
        for (String param : queryParamNames) {
            logRequest.queryParams.put(param, requestUri.getQueryParameter(param));
        }

        Headers headers = request.headers();
        logRequest.requestHeaders = new HashMap<>();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                logRequest.requestHeaders.put(headers.name(i), headers.value(i));
            }
        }

        if (requestBody != null) {
            if (requestBody.contentType() != null) {
                logRequest.requestContentType = requestBody.contentType().toString();
            }

            logRequest.requestBodySize = requestBody.contentLength();

            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            logRequest.requestBody = buffer.readString(charset);
        }

        logRequest.id = getDataBase().addHttpLogRequest(logRequest);
        logRequest.queryId = logRequest.id;
        logResponse.queryId = logRequest.queryId;

        long startTime = System.currentTimeMillis();
        logRequest.requestStartTime = startTime;

        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            logResponse.errorMessage = e.getMessage();
            getDataBase().addHttpLogResponse(logResponse);
            throw e;
        }

        logResponse.requestDuration = System.currentTimeMillis() - startTime;
        logResponse.code = String.valueOf(response.code());
        logResponse.message = response.message();
        logResponse.method = logRequest.method;
        logResponse.baseUrl = logRequest.baseUrl;
        logResponse.port = logRequest.port;
        logResponse.ip = logRequest.ip;
        logResponse.fullUrl = logRequest.fullUrl;
        logResponse.shortUrl = logRequest.shortUrl;

        Headers responseHeaders = response.headers();
        logResponse.responseHeaders = new HashMap<>();
        for (int i = 0, count = responseHeaders.size(); i < count; i++) {
            logResponse.responseHeaders.put(responseHeaders.name(i), responseHeaders.value(i));
        }

        ResponseBody responseBody = response.body();
        if (HttpHeaders.promisesBody(response) && responseBody != null) {
            long responseContentLength = responseBody.contentLength();
            logResponse.responseBodySize = responseContentLength;

            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();

            if ("gzip".equalsIgnoreCase(responseHeaders.get("Content-Encoding"))) {
                try (GzipSource gzippedResponseBody = new GzipSource(buffer.clone())) {
                    buffer = new Buffer();
                    buffer.writeAll(gzippedResponseBody);
                }
            }

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (responseContentLength != 0) {
                logResponse.responseBody = buffer.clone().readString(charset);
            }
        }

        getDataBase().addHttpLogResponse(logResponse);

        return response;
    }

    private ContinuousDataBaseManager getDataBase() {
        return ContinuousDataBaseManager.getInstance();
    }
}
