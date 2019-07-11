package com.sarproj.remotedebugger.logging;

import android.net.Uri;

import com.sarproj.remotedebugger.source.models.HttpLogModel;

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

    @Override
    @SuppressWarnings("ConstantConditions")
    public Response intercept(Chain chain) throws IOException {
        HttpLogModel httpLogModel = new HttpLogModel();

        Request request = chain.request();
        RequestBody requestBody = request.body();

        httpLogModel.method = request.method();
        httpLogModel.fullUrl = request.url().toString();
        httpLogModel.baseUrl = request.url().host();

        String query = request.url().url().getQuery();
        httpLogModel.shortUrl = request.url().url().getPath() + (query != null ? "?" + query : "");
        httpLogModel.port = request.url().port();

        InetAddress address = InetAddress.getByName(new URL(request.url().toString()).getHost());
        httpLogModel.ip = address.getHostAddress();

        httpLogModel.queryParams = new HashMap<>();
        Uri requestUri = Uri.parse(request.url().toString());
        Set<String> queryParamNames = requestUri.getQueryParameterNames();
        for (String param : queryParamNames) {
            httpLogModel.queryParams.put(param, requestUri.getQueryParameter(param));
        }

        Headers headers = request.headers();
        httpLogModel.requestHeaders = new HashMap<>();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                httpLogModel.requestHeaders.put(headers.name(i), headers.value(i));
            }
        }

        if (requestBody != null) {
            if (requestBody.contentType() != null) {
                httpLogModel.requestContentType = requestBody.contentType().toString();
            }

            httpLogModel.requestBodySize = requestBody.contentLength();

            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            httpLogModel.requestBody = buffer.readString(charset);
        }

        long startTime = System.currentTimeMillis();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            httpLogModel.errorMessage = e.getMessage();
            throw e;
        }

        httpLogModel.requestDuration = System.currentTimeMillis() - startTime;
        httpLogModel.requestStartTime = startTime;

        httpLogModel.code = response.code();
        httpLogModel.message = response.message();

        Headers responseHeaders = response.headers();
        httpLogModel.responseHeaders = new HashMap<>();
        for (int i = 0, count = responseHeaders.size(); i < count; i++) {
            httpLogModel.responseHeaders.put(responseHeaders.name(i), responseHeaders.value(i));
        }

        ResponseBody responseBody = response.body();

        if (HttpHeaders.hasBody(response) && responseBody != null) {
            long responseContentLength = responseBody.contentLength();
            httpLogModel.responseBodySize = responseContentLength;

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
                httpLogModel.responseBody = buffer.clone().readString(charset);
            }
        }

//        NetLogDataBaseManager.getInstance().getLastId(0);
//        NetLogDataBaseManager.getInstance().addLog(httpLogModel);
//        NetLogDataBaseManager.getInstance().getLastId(0);
//        NetLogDataBaseManager.getInstance().clearAll();

        return response;
    }
}
