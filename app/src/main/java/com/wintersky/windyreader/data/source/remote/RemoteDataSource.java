package com.wintersky.windyreader.data.source.remote;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.AppExecutors;

import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.RealmList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.wintersky.windyreader.util.Constants.is2String;
import static com.wintersky.windyreader.util.Constants.luaSafeDoString;
import static com.wintersky.windyreader.util.Constants.luaSafeRun;

@Singleton
public class RemoteDataSource implements DataSource {

    private final Context mContext;
    private final AppExecutors mExecutors;
    private final OkHttpClient mHttp;

    private Future taskCatalog;
    private Future taskChapter;

    @Inject
    RemoteDataSource(Context context, @NonNull AppExecutors executors, OkHttpClient mHttp) {
        mContext = context;
        mExecutors = executors;
        this.mHttp = mHttp;
    }

    @Override
    public void getBook(final String url, final GetBookCallback callback) {
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Book book = getBookFromRemote(url);
                    mExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onLoaded(book);
                        }
                    });
                } catch (final Exception e) {
                    mExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataNotAvailable(e);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getShelf(final @NonNull GetShelfCallback callback) {
        // none
    }

    @Override
    public void getCatalog(final String url, final GetCatalogCallback callback) {
        if (taskCatalog != null) {
            taskCatalog.cancel(true);
        }
        taskCatalog = mExecutors.networkIO().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Chapter> list = getCatalogFromRemote(url);
                    mExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onLoaded(list);
                            taskCatalog = null;
                        }
                    });
                } catch (final Exception e) {
                    mExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataNotAvailable(e);
                            taskCatalog = null;
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getChapter(final String url, final GetChapterCallback callback) {
        if (taskChapter != null) {
            taskChapter.cancel(true);
        }
        taskChapter = mExecutors.networkIO().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final Chapter chapter = getChapterFromRemote(url);
                    mExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onLoaded(chapter);
                            taskChapter = null;
                        }
                    });
                } catch (final Exception e) {
                    mExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataNotAvailable(e);
                            taskChapter = null;
                        }
                    });
                }
            }
        });
    }

    @Override
    public void saveBook(Book book) {
        // none
    }

    @Override
    public void updateCheck(String url, UpdateCheckCallback callback) {
        // none
    }

    private LuaState getLua(final Context context) throws Exception {
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

        luaState.setTop(0);
        luaState.getGlobal("debug");
        luaState.getField(-1, "traceback");

        luaSafeDoString(luaState, "require(\"windyreader_tools\")");

        return luaState;
    }

    @VisibleForTesting
    Book getBookFromRemote(String url) throws Exception {
        String fileName = url.split("/")[2].replace('.', '_') + ".lua";
        LuaState lua = getLua(mContext);

        Request request = new Request.Builder().url(url).build();
        Response response = mHttp.newCall(request).execute();
        byte[] bytes = response.body().bytes();
        String doc = new String(bytes, "UTF-8");
        if (doc.contains("charset=gbk")) {
            doc = new String(bytes, "GBK");
        }

        luaSafeDoString(lua, is2String(mContext.getAssets().open(fileName)));
        lua.getField(LuaState.LUA_GLOBALSINDEX, "getBook");
        lua.pushString(url);
        lua.pushString(doc);
        luaSafeRun(lua, 2, 1);
        String res = lua.toString(-1);
        try {
            return new Gson().fromJson(res, Book.class);
        } catch (Exception e) {
            throw new Exception(String.format("%s\n%s", res, e.getMessage()), e);
        }
    }

    @VisibleForTesting
    RealmList<Chapter> getCatalogFromRemote(String url) throws Exception {
        String fileName = url.split("/")[2].replace('.', '_') + ".lua";
        LuaState lua = getLua(mContext);

        Request request = new Request.Builder().url(url).build();
        Response response = mHttp.newCall(request).execute();
        byte[] bytes = response.body().bytes();
        String doc = new String(bytes, "UTF-8");
        if (doc.contains("charset=gbk")) {
            doc = new String(bytes, "GBK");
        }

        luaSafeDoString(lua, is2String(mContext.getAssets().open(fileName)));
        lua.getField(LuaState.LUA_GLOBALSINDEX, "getCatalog");
        lua.pushString(url);
        lua.pushString(doc);
        luaSafeRun(lua, 2, 1);
        String res = lua.toString(-1);
        try {
            return new Gson().fromJson(res, new TypeToken<RealmList<Chapter>>() {
            }.getType());
        } catch (Exception e) {
            throw new Exception(String.format("%s\n%s", res, e.getMessage()), e);
        }
    }

    @VisibleForTesting
    Chapter getChapterFromRemote(String url) throws Exception {
        String fileName = url.split("/")[2].replace('.', '_') + ".lua";
        LuaState lua = getLua(mContext);

        Request request = new Request.Builder().url(url).build();
        Response response = mHttp.newCall(request).execute();
        byte[] bytes = response.body().bytes();
        String doc = new String(bytes, "UTF-8");
        if (doc.contains("charset=gbk")) {
            doc = new String(bytes, "GBK");
        }

        luaSafeDoString(lua, is2String(mContext.getAssets().open(fileName)));
        lua.getField(LuaState.LUA_GLOBALSINDEX, "getChapter");
        lua.pushString(url);
        lua.pushString(doc);
        luaSafeRun(lua, 2, 1);
        String res = lua.toString(-1);
        try {
            return new Gson().fromJson(res, Chapter.class);
        } catch (Exception e) {
            throw new Exception(String.format("%s\n%s", res, e.getMessage()), e);
        }
    }
}
