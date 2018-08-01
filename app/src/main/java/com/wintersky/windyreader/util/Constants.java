package com.wintersky.windyreader.util;

import android.util.Log;

import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Constants {
    public static void WS(String hint, String msg) {
        if (hint == null) hint = " ";
        if (msg == null) msg = "null";
        String[] msgs = msg.split("\n");
        StringBuilder sb = new StringBuilder(hint);
        int len = 0;
        boolean canOut = false;
        for (String sub : msgs) {
            if (len + sub.length() < 3500) {
                sb.append("\n").append(sub);
                len += sub.length() + 5;
                canOut = true;
            } else {
                Log.i("wintersky", sb.toString());
                sb = new StringBuilder(hint);
                sb.append("\n").append(sub);
                len = sub.length() + 5;
                canOut = false;
            }
        }
        if (canOut)
            Log.i("wintersky", sb.toString());
    }

    public static void WS(String msg) {
        WS(" ", msg);
    }

    public static String is2String(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            is.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return bos.toString();
    }

    private static String errorReason(int error) {
        switch (error) {
            case 4:
                return "Out of memory";
            case 3:
                return "Syntax error";
            case 2:
                return "Runtime error";
            case 1:
                return "Yield error";
        }
        return "Unknown error " + error;
    }

    public static void luaSafeDoString(LuaState luaState, String src) throws LuaException {
        int ok = luaState.LloadString(src);
        if (ok == 0) {
            ok = luaState.pcall(0, 0, 0);
            if (ok == 0) {
                return;
            }
        }
        throw new LuaException(errorReason(ok) + ": " + luaState.toString(-1));
    }

    public static void luaSafeRun(LuaState luaState, int in, int out) throws LuaException {
        int ok = luaState.pcall(in, out, 0);
        if (ok != 0) {
            throw new LuaException(errorReason(ok) + ": " + luaState.toString(-1));
        }
    }
}
