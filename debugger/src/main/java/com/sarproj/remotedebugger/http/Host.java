package com.sarproj.remotedebugger.http;

public enum Host {
    INDEX("/", "index.html"),
    DATABASE("/database", "database.html"),
    LOGGING("/logging", "logging.html"),
    SHARED_REFERENCES("/shared-preferences", "shared-preferences.html"),
    ERROR_PAGE("/error", "error.html"),
    INDEX_STYLE("/css/index.css", "css/index.css"),
    ERROR_PAGE_STYLE("/css/error.css", "css/error.css"),
    DATABASE_STYLE("/css/database.css", "css/database.css"),
    LOGGING_STYLE("/css/logging.css", "css/logging.css"),
    SHARED_REFERENCES_STYLE("/css/shared-preferences.css", "css/shared-preferences.css");

    private String host;
    private String path;

    Host(String host, String path) {
        this.host = host;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static Host getHost(String url) {
        for (Host hosts : values()) {
            if (hosts.host.equalsIgnoreCase(url)) {
                return hosts;
            }
        }
        return null;
    }

    public boolean isCss() {
        return path.endsWith(".css");
    }

    public boolean isPng() {
        return path.endsWith(".png");
    }
}
