package com.wintersky.windyreader.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LogTools {
    public static void LOG(String hint, String msg) {
        if (hint != null) {
            Log.e("wintersky", ">>>>>>>>>> " + hint);
        }
        if (msg == null) msg = "log message null";

        List<String> logList = new ArrayList<>();
        String[] lines = msg.split("\n");
        StringBuilder sb = new StringBuilder(">>>>>>>>>>");
        int len = 0;
        for (String line : lines) {
            int lineLen = line.getBytes().length;
            if (len == 0 || len + lineLen < 3500) {
                sb.append("\n").append(line);
                len += lineLen + 5;
            } else {
                logList.add(sb.toString());
                sb = new StringBuilder("<<<<<<<<<<");
                sb.append("\n").append(line);
                len = lineLen + 5;
            }
        }
        logList.add(sb.toString());
        for (String part : logList) {
            Log.d("wintersky", part);
        }
    }

    public static void LOG(String msg) {
        LOG(null, msg);
    }
}
