package com.wintersky.windyreader.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.wintersky.windyreader.util.LogUtil.LOGD;

public class FileUtil {

    public static String is2String(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len;
        while ((len = is.read(buff)) != -1) {
            bos.write(buff, 0, len);
        }
        return bos.toString();
    }

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
