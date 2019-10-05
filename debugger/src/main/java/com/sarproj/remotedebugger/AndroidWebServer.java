package com.sarproj.remotedebugger;

import android.content.Context;
import android.content.res.AssetManager;

import com.sarproj.remotedebugger.api.base.Api;
import com.sarproj.remotedebugger.api.database.DatabaseApi;
import com.sarproj.remotedebugger.api.error.ErrorPage;
import com.sarproj.remotedebugger.api.log.LogApi;
import com.sarproj.remotedebugger.api.network.NetworkApi;
import com.sarproj.remotedebugger.api.sharedprefs.SharedPrefsApi;
import com.sarproj.remotedebugger.http.Host;
import com.sarproj.remotedebugger.http.HttpResponse;
import com.sarproj.remotedebugger.settings.InternalSettings;
import com.sarproj.remotedebugger.utils.FileUtils;
import com.sarproj.remotedebugger.utils.InternalUtils;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

final class AndroidWebServer extends NanoHTTPD {
    private final Context context;
    private final AssetManager assetManager;
    private Api logApi;
    private Api databaseApi;
    private Api sharedPrefsApi;
    private Api networkApi;
    private ErrorPage errorPage;
    private InternalSettings internalSettings;

    AndroidWebServer(Context context, String hostname, int port, InternalSettings internalSettings) {
        super(hostname, port);
        this.context = context;
        this.internalSettings = internalSettings;
        assetManager = context.getAssets();
    }

    @Override
    public Response serve(IHTTPSession session) {
        final Method method = session.getMethod();
        final Host host = Host.getHost(session.getUri());

        if (host == null) {
            return getErrorPageResponse(Response.Status.NOT_FOUND, "Sorry we could not find that page");
        }

        if (Method.GET.equals(method)) {
            return getResponse(host, session.getParameters());
        } else if (Method.POST.equals(method)) {
            Response response = parseParams(session);
            if (response == null) {
                return getResponse(host, session.getParameters());
            }
            return response;
        } else {
            return getErrorPageResponse(Response.Status.FORBIDDEN, "Forbidden. Sorry you do not have access");
        }
    }

    private Response getResponse(Host host, Map<String, List<String>> params) {
        try {
            if (host == Host.INDEX) {
                destroyLogApi();
                destroyDatabaseApi();
                destroySharedPrefsApi();
                destroyErrorPage();
                destroyNetworkApi();
                return getSimpleResponse(Host.INDEX);
            } else if (host == Host.LOGGING) {
                destroyDatabaseApi();
                destroySharedPrefsApi();
                destroyErrorPage();
                destroyNetworkApi();
                return HttpResponse.newFixedLengthResponse(getLogApi().execute(params));
            } else if (host == Host.DATABASE) {
                destroyLogApi();
                destroySharedPrefsApi();
                destroyErrorPage();
                destroyNetworkApi();
                return HttpResponse.newFixedLengthResponse(getDatabaseApi().execute(params));
            } else if (host == Host.SHARED_REFERENCES) {
                destroyDatabaseApi();
                destroyLogApi();
                destroyNetworkApi();
                destroyErrorPage();
                return HttpResponse.newFixedLengthResponse(getSharedPrefsApi().execute(params));
            } else if (host == Host.NETWORK) {
                destroyDatabaseApi();
                destroyLogApi();
                destroySharedPrefsApi();
                destroyErrorPage();
                return HttpResponse.newFixedLengthResponse(getNetworkApi().execute(params));
            } else if (host.isCss()) {
                return getCssResponse(host);
            } else if (host.isPng()) {
                return getPngResponse(host);
            } else {
                return getErrorPageResponse(Response.Status.NO_CONTENT,
                        Response.Status.NO_CONTENT.getDescription());
            }
        } catch (ResponseException ex) {
            return HttpResponse.newErrorResponse(ex.getStatus(),
                    ex.getMessage() + "\n" + InternalUtils.getStackTrace(ex));
        } catch (Exception ex) {
            return HttpResponse.newErrorResponse(Response.Status.BAD_REQUEST,
                    ex.getMessage() + "\n" + InternalUtils.getStackTrace(ex));
        } catch (Throwable th) {
            return HttpResponse.newErrorResponse(Response.Status.INTERNAL_ERROR,
                    th.getMessage() + "\n" + InternalUtils.getStackTrace(th));
        }
    }

    private Response getErrorPageResponse(Response.Status status, String description) {
        return HttpResponse.newFixedLengthResponse(
                getErrorPage().get(status.getRequestStatus(), description));
    }

    private Response getCssResponse(Host host) {
        try {
            return HttpResponse.newCssResponse(
                    FileUtils.getStreamFromAssets(assetManager, host.getPath()));
        } catch (Exception ex) {
            return getErrorPageResponse(Response.Status.INTERNAL_ERROR,
                    String.format("Server internal error: %s", ex.getMessage()));
        }
    }

    private Response getPngResponse(Host host) {
        try {
            return HttpResponse.newPngResponse(
                    FileUtils.getStreamFromAssets(assetManager, host.getPath()));
        } catch (Exception ex) {
            return getErrorPageResponse(Response.Status.INTERNAL_ERROR,
                    String.format("Server internal error: %s", ex.getMessage()));
        }
    }

    private Response getSimpleResponse(Host host) {
        return HttpResponse.newFixedLengthResponse(
                FileUtils.getTextFromAssets(assetManager, host.getPath()));
    }

    private Response parseParams(IHTTPSession session) {
        try {
            session.parseBody(null);
            return null;
        } catch (Exception ex) {
            return getErrorPageResponse(Response.Status.INTERNAL_ERROR,
                    String.format("Server internal error: %s", ex.getMessage()));
        }
    }

    private Api getLogApi() {
        if (logApi == null) {
            logApi = new LogApi(context, internalSettings);
        }
        return logApi;
    }

    private ErrorPage getErrorPage() {
        if (errorPage == null) {
            errorPage = new ErrorPage(context);
        }
        return errorPage;
    }

    private Api getDatabaseApi() {
        if (databaseApi == null) {
            databaseApi = new DatabaseApi(context, internalSettings);
        }
        return databaseApi;
    }

    private Api getSharedPrefsApi() {
        if (sharedPrefsApi == null) {
            sharedPrefsApi = new SharedPrefsApi(context, internalSettings);
        }
        return sharedPrefsApi;
    }

    private Api getNetworkApi() {
        if (networkApi == null) {
            networkApi = new NetworkApi(context, internalSettings);
        }
        return networkApi;
    }

    private void destroyNetworkApi() {
        if (networkApi != null) {
            networkApi.destroy();
            networkApi = null;
        }
    }

    private void destroyLogApi() {
        if (logApi != null) {
            logApi.destroy();
            logApi = null;
        }
    }

    private void destroyDatabaseApi() {
        if (databaseApi != null) {
            databaseApi.destroy();
            databaseApi = null;
        }
    }

    private void destroySharedPrefsApi() {
        if (sharedPrefsApi != null) {
            sharedPrefsApi.destroy();
            sharedPrefsApi = null;
        }
    }

    private void destroyErrorPage() {
        if (errorPage != null) {
            errorPage.destroy();
            errorPage = null;
        }
    }
}
