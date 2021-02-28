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
package zerobranch.androidremotedebugger;

import android.content.Context;

import zerobranch.androidremotedebugger.logging.Logger;

public final class AndroidRemoteDebugger {

    private AndroidRemoteDebugger(Builder builder) {
    }

    public synchronized static void init(Context context) {
    }

    public synchronized static void init(final AndroidRemoteDebugger androidRemoteDebugger) {
    }

    public synchronized static void stop() {
    }

    public static boolean isEnable() {
        return false;
    }

    public static boolean isAliveWebServer() {
        return false;
    }

    public static class Builder {

        public Builder(Context context) {
        }

        public Builder enabled(boolean enabled) {
            return this;
        }

        public Builder disableInternalLogging() {
            return this;
        }

        public Builder enableDuplicateLogging() {
            return this;
        }

        public Builder enableDuplicateLogging(Logger logger) {
            return this;
        }

        public Builder disableJsonPrettyPrint() {
            return this;
        }

        public Builder disableNotifications() {
            return this;
        }

        public Builder excludeUncaughtException() {
            return this;
        }

        public Builder port(int port) {
            return this;
        }

        public AndroidRemoteDebugger build() {
            return new AndroidRemoteDebugger(this);
        }
    }

    public static class Log {
        public static void v(Throwable th) {
        }

        public static void v(String msg) {
        }

        public static void v(String tag, String msg) {
        }

        public static void v(String tag, String msg, Throwable th) {
        }

        public static void d(Throwable th) {
        }

        public static void d(String msg) {
        }

        public static void d(String tag, String msg) {
        }

        public static void d(String tag, String msg, Throwable th) {
        }

        public static void i(Throwable th) {
        }

        public static void i(String msg) {
        }

        public static void i(String tag, String msg) {
        }

        public static void i(String tag, String msg, Throwable th) {
        }

        public static void w(Throwable th) {
        }

        public static void w(String msg) {
        }

        public static void w(String tag, String msg) {
        }

        public static void w(String tag, String msg, Throwable th) {
        }

        public static void e(Throwable th) {
        }

        public static void e(String msg) {
        }

        public static void e(String tag, String msg) {
        }

        public static void e(String tag, String msg, Throwable th) {
        }

        public static void wtf(Throwable th) {
        }

        public static void wtf(String msg) {
        }

        public static void wtf(String tag, String msg) {
        }

        public static void wtf(String tag, String msg, Throwable th) {
        }

        public static void log(int priority, String tag, String msg, Throwable th) {
        }
    }
}

