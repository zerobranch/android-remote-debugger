package com.sarproj.remotedebugger.utils;

public final class HtmlUtils {

    // todo избавиться от этого
    public static int getIndexByTag(StringBuilder content, String tag) {
        tag = "\"".concat(tag).concat("\"");
        int index = content.indexOf(tag);

        if (index == -1) {
            return -1;
        }

        index += tag.length();

        for (int i = index; i < content.length(); i++) {
            if (content.charAt(i) == '>') {
                index = i + 1;
                break;
            }
        }
        return index;
    }
}
