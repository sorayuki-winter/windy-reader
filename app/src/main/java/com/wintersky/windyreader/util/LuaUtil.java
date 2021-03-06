package com.wintersky.windyreader.util;

import android.content.Context;

import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class LuaUtil {

    public static LuaState getLua(final Context context) throws LuaException {
        final LuaState luaState = LuaStateFactory.newLuaState();
        luaState.openLibs();

        JavaFunction assetLoader = new JavaFunction(luaState) {
            @Override
            public int execute() {
                String name = luaState.toString(-1).replace(".", "/");
                try {
                    InputStream is = context.getAssets().open(name + ".lua");
                    int available = is.available();
                    byte[] buff = new byte[available];
                    int len = is.read(buff);
                    if (len != available) {
                        throw new IOException(String.format("file length available %d but read %d", available, len));
                    }
                    luaState.LloadBuffer(buff, name);
                    return 1;
                } catch (IOException e) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(os));
                    luaState.pushString("Cannot load module " + name + ":\n" + os.toString());
                    return 1;
                }
            }
        };

        luaState.getGlobal("package");            // package
        luaState.getField(-1, "loaders");         // package loaders
        int nLoaders = luaState.objLen(-1);       // package loaders

        luaState.pushJavaFunction(assetLoader);   // package loaders loader
        luaState.rawSetI(-2, nLoaders + 1);       // package loaders
        luaState.pop(1);                          // package

        luaState.getField(-1, "path");            // package path
        String customPath = context.getFilesDir() + "/?.lua";
        luaState.pushString(";" + customPath);    // package path custom
        luaState.concat(2);                       // package pathCustom
        luaState.setField(-2, "path");            // package
        luaState.pop(1);

        luaState.getGlobal("debug");
        luaState.getField(-1, "traceback");
        luaState.insert(1);
        luaState.setTop(1);

        return luaState;
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

    public static void luaSafeDoString(LuaState luaState, String src, int out) throws LuaException {
        int ok = luaState.LloadString(src);
        if (ok != 0) {
            throw new LuaException(errorReason(ok) + ": " + luaState.toString(-1));
        }
        luaSafeRun(luaState, 0, out);
    }

    public static void luaSafeRun(LuaState luaState, int in, int out) throws LuaException {
        int ok = luaState.pcall(in, out, 1);
        if (ok != 0) {
            throw new LuaException(errorReason(ok) + ": " + luaState.toString(-1));
        }
    }
}
