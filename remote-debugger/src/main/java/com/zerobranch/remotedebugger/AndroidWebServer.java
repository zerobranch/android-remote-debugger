package com.zerobranch.remotedebugger;

import android.content.Context;
import android.content.res.AssetManager;

import com.zerobranch.remotedebugger.api.base.Controller;
import com.zerobranch.remotedebugger.api.database.DatabaseController;
import com.zerobranch.remotedebugger.api.home.HomeController;
import com.zerobranch.remotedebugger.api.log.LogController;
import com.zerobranch.remotedebugger.api.network.NetworkController;
import com.zerobranch.remotedebugger.api.sharedprefs.SharedPrefsController;
import com.zerobranch.remotedebugger.http.Host;
import com.zerobranch.remotedebugger.http.HttpResponse;
import com.zerobranch.remotedebugger.settings.InternalSettings;
import com.zerobranch.remotedebugger.utils.FileUtils;
import com.zerobranch.remotedebugger.utils.InternalUtils;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

final class AndroidWebServer extends NanoHTTPD {
    private final Context context;
    private final AssetManager assetManager;
    private Controller homeController;
    private Controller logController;
    private Controller databaseController;
    private Controller sharedPrefsController;
    private Controller networkController;
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
                return HttpResponse.newFixedLengthResponse(getHomeController().execute(params));
            } else if (host == Host.LOGGING) {
                return HttpResponse.newFixedLengthResponse(getLogController().execute(params));
            } else if (host == Host.DATABASE) {
                return HttpResponse.newFixedLengthResponse(getDatabaseController().execute(params));
            } else if (host == Host.SHARED_REFERENCES) {
                return HttpResponse.newFixedLengthResponse(getSharedPrefsController().execute(params));
            } else if (host == Host.NETWORK) {
                return HttpResponse.newFixedLengthResponse(getNetworkController().execute(params));
            } else if (host.isCss()) {
                return getCssResponse(host);
            } else if (host.isPng()) {
                return getPngResponse(host);
            } else {
                return getErrorPageResponse(Response.Status.NO_CONTENT,
                        Response.Status.NO_CONTENT.getDescription());
            }
        } catch (ResponseException ex) {
            return getErrorPageResponse(ex.getStatus(),
                    ex.getMessage() + "\n" + InternalUtils.getStackTrace(ex));
        } catch (Exception ex) {
            return getErrorPageResponse(Response.Status.BAD_REQUEST,
                    ex.getMessage() + "\n" + InternalUtils.getStackTrace(ex));
        } catch (Throwable th) {
            return getErrorPageResponse(Response.Status.INTERNAL_ERROR,
                    th.getMessage() + "\n" + InternalUtils.getStackTrace(th));
        }
    }

    private Response getErrorPageResponse(Response.Status status, String description) {
        return HttpResponse.newErrorResponse(status, description);
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

    private Response parseParams(IHTTPSession session) {
        try {
            session.parseBody(null);
            return null;
        } catch (Exception ex) {
            return getErrorPageResponse(Response.Status.INTERNAL_ERROR,
                    String.format("Server internal error: %s", ex.getMessage()));
        }
    }

    private Controller getLogController() {
        if (logController == null) {
            logController = new LogController(context, internalSettings);
        }
        return logController;
    }

    private Controller getDatabaseController() {
        if (databaseController == null) {
            databaseController = new DatabaseController(context, internalSettings);
        }
        return databaseController;
    }

    private Controller getHomeController() {
        if (homeController == null) {
            homeController = new HomeController(context, internalSettings);
        }
        return homeController;
    }

    private Controller getSharedPrefsController() {
        if (sharedPrefsController == null) {
            sharedPrefsController = new SharedPrefsController(context, internalSettings);
        }
        return sharedPrefsController;
    }

    private Controller getNetworkController() {
        if (networkController == null) {
            networkController = new NetworkController(context, internalSettings);
        }
        return networkController;
    }
}
