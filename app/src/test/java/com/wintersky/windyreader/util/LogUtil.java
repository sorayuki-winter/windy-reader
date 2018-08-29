package com.wintersky.windyreader.util;

import java.util.ArrayList;
import java.util.List;

public class LogUtil {
    public static void LOG(String hint, String msg) {
        if (hint != null) {
            System.out.println(">>>>>>>>>> " + hint);
        }
        if (msg == null) {
            msg = "log message null";
        }

        List<String> logList = new ArrayList<>();
        String[] lines = msg.split("\n");
        StringBuilder sb = new StringBuilder(">>>>>>>>>>");
        int len = 0;
        for (String line : lines) {
            if (len == 0 || len + line.length() < 3500) {
                sb.append("\n").append(line);
                len += line.length() + 5;
            } else {
                logList.add(sb.toString());
                sb = new StringBuilder("<<<<<<<<<<");
                sb.append("\n").append(line);
                len = line.length() + 5;
            }
        }
        logList.add(sb.toString());
        for (String part : logList) {
            System.out.println(part);
        }
    }

    public static void LOG(String msg) {
        LOG(null, msg);
    }
}
