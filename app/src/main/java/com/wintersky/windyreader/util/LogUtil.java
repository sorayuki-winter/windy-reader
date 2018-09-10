package com.wintersky.windyreader.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class LogUtil {

    public static void LOG(String hint, Throwable error) {
        if (hint != null) {
            LOGE(hint);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        error.printStackTrace(new PrintStream(bos));
        LOGD(null, bos.toString());
    }

    public static void LOG(Throwable error) {
        LOG(error.getMessage(), error);
    }

    public static void LOG(String hint, String msg) {
        if (hint != null) {
            LOGE(hint);
        }
        for (String part : msgSplit(msg == null ? "MSG NULL" : msg, ">\n")) {
            Log.d("wintersky", part);
        }
    }

    public static void LOG(String msg) {
        LOGD(null, msg);
    }

    public static void LOGD(String hint, String msg) {
        if (hint != null) {
            LOGD(hint);
        }
        for (String part : msgSplit(msg == null ? "MSG NULL" : msg, ">\n")) {
            Log.d("wintersky", part);
        }
    }

    public static void LOGD(String hint) {
        for (String part : msgSplit(hint == null ? "" : hint, "> ")) {
            Log.d("wintersky", part);
        }
    }

    public static void LOGE(String hint, String msg) {
        if (hint != null) {
            LOGE(hint);
        }
        for (String part : msgSplit(msg == null ? "MSG NULL" : msg, ">\n")) {
            Log.e("wintersky", part);
        }
    }

    public static void LOGE(String hint) {
        for (String part : msgSplit(hint == null ? "" : hint, "> ")) {
            Log.e("wintersky", part);
        }
    }

    public static List<String> msgSplit(String msg, String prefix) {
        List<String> logList = new ArrayList<>();
        String[] lines = msg.split("\n");
        StringBuilder sb = new StringBuilder(prefix);
        int len = 0;
        for (String line : lines) {
            int lineLen = line.getBytes().length;
            if (len == 0 || len + lineLen < 3500) {
                sb.append(line).append("\n");
                len += lineLen + 5;
            } else {
                logList.add(sb.toString());
                sb = new StringBuilder(prefix);
                sb.append(line).append("\n");
                len = lineLen + 5;
            }
        }
        logList.add(sb.toString());
        return logList;
    }
}
