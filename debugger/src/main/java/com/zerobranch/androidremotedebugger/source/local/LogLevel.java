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
package com.zerobranch.androidremotedebugger.source.local;

public enum LogLevel {
    VERBOSE(2),
    DEBUG(3),
    INFO(4),
    WARN(5),
    ERROR(6),
    FATAL(7);

    private final int priority;

    LogLevel(int priority) {
        this.priority = priority;
    }

    public int priority() {
        return priority;
    }

    public static LogLevel getByPriority(int priority) {
        for (LogLevel logLevel : LogLevel.values()) {
            if (logLevel.priority == priority) {
                return logLevel;
            }
        }

        return null;
    }
}