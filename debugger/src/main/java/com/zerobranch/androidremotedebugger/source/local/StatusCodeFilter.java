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

import android.text.TextUtils;

public class StatusCodeFilter {
    public int minStatusCode = -1;
    public int maxStatusCode = -1;

    public StatusCodeFilter(String rawStatusCodeFilter) {
        if (TextUtils.isEmpty(rawStatusCodeFilter)) {
            return;
        }

        String[] splitData = rawStatusCodeFilter.split("[ ,\\-]");

        for (String item : splitData) {
            try {
                if (minStatusCode == -1) {
                    minStatusCode = Integer.parseInt(item);
                } else if (maxStatusCode == -1) {
                    maxStatusCode = Integer.parseInt(item);
                } else {
                    return;
                }
            } catch (NumberFormatException ignored) { }
        }

        if (minStatusCode != -1 && maxStatusCode == -1) {
            maxStatusCode = minStatusCode;
        }
    }

    public boolean isExistCondition() {
        return minStatusCode != -1 && maxStatusCode != -1;
    }
}
