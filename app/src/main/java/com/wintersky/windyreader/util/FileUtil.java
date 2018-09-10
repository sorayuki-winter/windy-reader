package com.wintersky.windyreader.util;

import java.io.File;

import static com.wintersky.windyreader.util.LogUtil.LOGD;

public class FileUtil {

    public static boolean delete(File dir) {
        if (!dir.exists()) {
            return false;
        }
        if (dir.isFile()) {
            return dir.delete();
        }
        for (File file : dir.listFiles()) {
            if (!delete(file)) {
                LOGD(file.isDirectory() ? "Directory" : "File" + " delete fail: " + file.getAbsolutePath());
            }
        }
        return dir.delete();
    }
}
