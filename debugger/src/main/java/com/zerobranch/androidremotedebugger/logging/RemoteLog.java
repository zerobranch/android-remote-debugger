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
package com.zerobranch.androidremotedebugger.logging;

import android.os.Build;

import com.zerobranch.androidremotedebugger.source.local.LogLevel;
import com.zerobranch.androidremotedebugger.source.managers.ContinuousDBManager;
import com.zerobranch.androidremotedebugger.source.models.LogModel;
import com.zerobranch.androidremotedebugger.utils.InternalUtils;

import org.jetbrains.annotations.NotNull;

public final class RemoteLog {
    private static final String DEFAULT_TAG = RemoteLog.class.getSimpleName();
    private static final int MAX_LOG_LENGTH = 2000;
    private static final int MAX_TAG_LENGTH = 23;
    private final ContinuousDBManager continuousDBManager;
    private final Logger logger;

    public RemoteLog(Logger logger) {
        this.logger = logger;
        continuousDBManager = ContinuousDBManager.getInstance();
    }

    public void log(LogLevel logLevel, String tag, String msg, Throwable th) {
        if (tag == null) {
            tag = DEFAULT_TAG;
        }

        if (msg != null && msg.length() == 0) {
            msg = null;
        }

        if (msg == null) {
            if (th == null) {
                return;
            }
            msg = InternalUtils.getStackTrace(th);
        } else {
            if (th != null) {
                msg += "\n" + InternalUtils.getStackTrace(th);
            }
        }

        continuousDBManager.addLog(new LogModel(logLevel.name(), tag, msg, System.currentTimeMillis()));

        if (logger != null) {
            if (logger instanceof DefaultLogger) {
                if (tag.length() > MAX_TAG_LENGTH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    tag = tag.substring(0, MAX_TAG_LENGTH);
                }

                partialLogs(logLevel.priority(), tag, msg, th);
            } else {
                logger.log(logLevel.priority(), tag, msg, th);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void partialLogs(int priority, String tag, @NotNull String msg, Throwable th) {
        if (msg.length() < MAX_LOG_LENGTH) {
            logger.log(priority, tag, msg, th);
            return;
        }

        for (int i = 0, length = msg.length(); i < length; i++) {
            int newline = msg.indexOf('\n', i);
            newline = newline != -1 ? newline : length;
            do {
                int end = Math.min(newline, i + MAX_LOG_LENGTH);
                String part = msg.substring(i, end);
                logger.log(priority, tag, part, th);
                i = end;
            } while (i < newline);
        }
    }
}
