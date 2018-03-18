package com.wintersky.windyreader.util;

import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Constants {
    public static String LT = "wintersky";

    public static Integer getInteger(final String s) {
        if (s == null) return null;
        String[] ss = s.split("[^0-9]+");
        if (ss.length < 1) return null;
        for (String s1 : ss) {
            if (!s1.equals("")) return Integer.valueOf(s1);
        }
        return null;
    }

    public static String is2String(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
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
            luaState.getGlobal("debug");
            luaState.getField(-1, "traceback");
            luaState.remove(-2);
            luaState.insert(-2);
            ok = luaState.pcall(0, 0, -2);
        }
        if (ok != 0) {
            throw new LuaException(errorReason(ok) + ": " + luaState.toString(-1));
        }
    }

    public static void luaSafeRun(LuaState luaState) throws LuaException {
        luaState.getGlobal("debug");
        luaState.getField(-1, "traceback");
        luaState.remove(-2);
        luaState.insert(-2);
        int ok = luaState.pcall(0, 0, -2);
        if (ok != 0) {
            throw new LuaException(errorReason(ok) + ": " + luaState.toString(-1));
        }
    }
}
