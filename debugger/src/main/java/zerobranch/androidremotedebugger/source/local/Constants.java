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
package zerobranch.androidremotedebugger.source.local;

import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Constants {
    SimpleDateFormat defaultDateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH);
    int LIMIT_HTTP_LOGS_PACKS = 500;
    int LIMIT_LOGS_PACKS = 1000;
}
