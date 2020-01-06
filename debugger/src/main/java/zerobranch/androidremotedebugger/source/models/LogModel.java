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
package zerobranch.androidremotedebugger.source.models;

import zerobranch.androidremotedebugger.source.local.Constants;

public class LogModel {
    public String time;
    public String level;
    public String tag;
    public String message;

    public LogModel() {
    }

    public LogModel(String level, String tag, String message, long currentTime) {
        this.level = level;
        this.tag = tag;
        this.message = message;
        this.time = Constants.defaultDateFormat.format(currentTime);
    }
}
