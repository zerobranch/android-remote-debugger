package com.sarproj.remotedebugger.api.error;

import android.content.Context;

import com.sarproj.remotedebugger.http.Host;
import com.sarproj.remotedebugger.utils.FileUtils;
import com.sarproj.remotedebugger.utils.HtmlUtils;

import static com.sarproj.remotedebugger.api.error.ErrorHtmlKey.ERROR_CODE_ID;
import static com.sarproj.remotedebugger.api.error.ErrorHtmlKey.ERROR_CODE_TAG;
import static com.sarproj.remotedebugger.api.error.ErrorHtmlKey.ERROR_TEXT_ID;

public class ErrorPage {
    private Context context;

    public ErrorPage(Context context) {
        this.context = context;
    }

    public String get(int errorStatus, String errorMessage) {
        StringBuilder errorContent = new StringBuilder(
                FileUtils.getTextFromAssets(context.getAssets(), Host.ERROR_PAGE.getPath()));

        final String errorCode = String.valueOf(errorStatus);
        int insertIndex = HtmlUtils.getIndexByTag(errorContent, ERROR_CODE_ID);

        if (insertIndex != -1 && errorCode.length() > 2) {
            errorContent.insert(insertIndex, String.format(ERROR_CODE_TAG, errorCode.charAt(0),
                    errorCode.charAt(1), errorCode.charAt(2)));
        }

        insertIndex = HtmlUtils.getIndexByTag(errorContent, ERROR_TEXT_ID);
        if (insertIndex != -1) {
            errorContent.insert(insertIndex, errorMessage);
        }

        return errorContent.toString();
    }

    public void destroy() {
        context = null;
    }
}
