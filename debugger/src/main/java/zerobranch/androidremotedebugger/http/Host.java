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
package zerobranch.androidremotedebugger.http;

public enum Host {
    INDEX("/", "index.html"),
    DATABASE("/database", "database.html"),
    LOGGING("/logging", "logging.html"),
    SHARED_REFERENCES("/shared-preferences", "shared-preferences.html"),
    NETWORK("/network", "network.html"),
    INDEX_STYLE("/css/index.css", "css/index.css"),
    DATABASE_STYLE("/css/database.css", "css/database.css"),
    LOGGING_STYLE("/css/logging.css", "css/logging.css"),
    NETWORK_STYLE("/css/network.css", "css/network.css"),
    SHARED_REFERENCES_STYLE("/css/shared-preferences.css", "css/shared-preferences.css");

    private final String host;
    private final String path;

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
