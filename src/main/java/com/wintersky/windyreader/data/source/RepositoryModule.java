package com.wintersky.windyreader.data.source;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import com.wintersky.windyreader.data.source.local.LocalDataSource;
import com.wintersky.windyreader.data.source.remote.RemoteDataSource;
import com.wintersky.windyreader.util.AppExecutors;
import com.wintersky.windyreader.util.DiskIOThreadExecutor;

import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import static com.wintersky.windyreader.util.Constants.WS;
import static com.wintersky.windyreader.util.Constants.is2String;
import static com.wintersky.windyreader.util.Constants.luaSafeDoString;

/**
 * This is used by Dagger to inject the required arguments into the {@link Repository}.
 */
@Module
abstract public class RepositoryModule {

    private static final int THREAD_COUNT = 3;

    @Provides
    static SharedPreferences provideSP(Context context) {
        return context.getSharedPreferences("Book", Context.MODE_PRIVATE);
    }

    @Singleton
    @Provides
    static AppExecutors provideAppExecutors() {
        return new AppExecutors(new DiskIOThreadExecutor(),
                Executors.newFixedThreadPool(THREAD_COUNT),
                new AppExecutors.MainThreadExecutor());
    }

    @Singleton
    @Provides
    static LuaState provideLuaState(final Context context) {
        final LuaState luaState = LuaStateFactory.newLuaState();
        luaState.openLibs();

        JavaFunction assetLoader = new JavaFunction(luaState) {
            @Override
            public int execute() {
                String name = luaState.toString(-1);

                AssetManager am = context.getAssets();
                try {
                    InputStream is = am.open(name + ".lua");
                    byte[] bytes = is2String(is).getBytes();
                    luaState.LloadBuffer(bytes, name);
                    return 1;
                } catch (Exception e) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(os));
                    luaState.pushString("Cannot load module " + name + ":\n" + os.toString());
                    return 1;
                }
            }
        };

        try {
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

            luaSafeDoString(luaState, "require(\"windyreader_tools\")");
        } catch (LuaException e) {
            String msg = "LuaState init fail\n" + e + "\n";
            msg += e.getStackTrace()[0].toString();
            WS(msg);
            return null;
        }
        return luaState;
    }

    @Singleton
    @Binds
    @Local
    abstract DataSource provideBooksLocalDataSource(LocalDataSource dataSource);

    @Singleton
    @Binds
    @Remote
    abstract DataSource provideBooksRemoteDataSource(RemoteDataSource dataSource);
}
