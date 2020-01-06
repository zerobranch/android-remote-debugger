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
package zerobranch.androidremotedebugger.api.home;

import android.content.Context;

import zerobranch.androidremotedebugger.api.base.Controller;
import zerobranch.androidremotedebugger.http.Host;
import zerobranch.androidremotedebugger.settings.InternalSettings;
import zerobranch.androidremotedebugger.source.models.Settings;
import zerobranch.androidremotedebugger.utils.FileUtils;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HomeController extends Controller {

    public HomeController(Context context, InternalSettings internalSettings) {
        super(context, internalSettings);
    }

    @Override
    public String execute(Map<String, List<String>> params) throws NanoHTTPD.ResponseException {
        if (params == null || params.isEmpty()) {
            return FileUtils.getTextFromAssets(context.getAssets(), Host.INDEX.getPath());
        } else if (params.containsKey(HomeKey.GET_SETTINGS)) {
            return getSettings();
        }
        return EMPTY;
    }

    private String getSettings() {
        return serialize(new Settings(context.getPackageName()));
    }
}
