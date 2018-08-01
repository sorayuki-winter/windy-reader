package com.wintersky.windyreader.data.source.remote;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.AppExecutors;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
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

import static com.wintersky.windyreader.util.Constants.WS;
import static com.wintersky.windyreader.util.Constants.is2String;
import static com.wintersky.windyreader.util.Constants.luaSafeDoString;
import static com.wintersky.windyreader.util.Constants.luaSafeRun;

@Singleton
public class RemoteDataSource implements DataSource {

    private final AppExecutors mExecutors;
    private final Context mContext;

    private Future taskCatalog;
    private Future taskChapter;

    @Inject
    RemoteDataSource(Context context, @NonNull AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
    }

    @Override
    public void getBook(final String url, final GetBookCallback callback) {
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                final Book book = getBookFromRemote(url);
                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (book == null) {
                            callback.onDataNotAvailable();
                        } else {
                            callback.onLoaded(book);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void getLList(GetLListCallback callback) {
        // none
    }

    /**
     * Note: {@link GetShelfCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
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
                final List<Chapter> list = getCatalogFromRemote(url);
                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (list != null && !list.isEmpty()) {
                            callback.onLoaded(list);
                        } else {
                            callback.onDataNotAvailable();
                        }
                        taskCatalog = null;
                    }
                });
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
                final Chapter chapter = getChapterFromRemote(url);
                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (chapter == null) {
                            callback.onDataNotAvailable();
                        } else {
                            callback.onLoaded(chapter);
                        }
                        taskChapter = null;
                    }
                });
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

    private LuaState getLua(final Context context) {
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

            luaState.setTop(0);
            luaState.getGlobal("debug");
            luaState.getField(-1, "traceback");

            luaSafeDoString(luaState, "require(\"windyreader_tools\")");
        } catch (LuaException e) {
            String msg = "LuaState init fail\n" + e + "\n";
            msg += e.getStackTrace()[0].toString();
            WS(msg);
            return null;
        }
        return luaState;
    }

    @VisibleForTesting
    Book getBookFromRemote(String url) {
        Book book = new Book();
        book.setUrl(url);
        String fileName = url.split("/")[2] + ".lua";
        LuaState lua = getLua(mContext);
        if (lua == null) return null;
        try {
            luaSafeDoString(lua, is2String(mContext.getAssets().open(fileName)));
            lua.getField(LuaState.LUA_GLOBALSINDEX, "getBook");
            lua.pushString(url);
            lua.pushJavaObject(book);
            luaSafeRun(lua, 2, 0);
        } catch (Exception e) {
            String msg = "get book fail\n" + e + "\n";
            msg += e.getStackTrace()[0].toString();
            WS(msg);
            return null;
        }
        return book;
    }

    @VisibleForTesting
    RealmList<Chapter> getCatalogFromRemote(String url) {
        String fileName = url.split("/")[2] + ".lua";
        LuaState lua = getLua(mContext);
        if (lua == null) return null;
        try {
            luaSafeDoString(lua, is2String(mContext.getAssets().open(fileName)));
            lua.getField(LuaState.LUA_GLOBALSINDEX, "getCatalog");
            lua.pushString(url);
            luaSafeRun(lua, 1, 1);
            Elements list = (Elements) lua.toJavaObject(-1);
            RealmList<Chapter> catalog = new RealmList<>();
            for (int i = 0; i < list.size(); i++) {
                Chapter chapter = new Chapter();
                Element aC = list.get(i);
                chapter.setIndex(i);
                chapter.setTitle(aC.attr("title"));
                chapter.setUrl(aC.absUrl("href"));
                catalog.add(chapter);
            }
            return catalog;
        } catch (Exception e) {
            String msg = e + "\n";
            msg += e.getStackTrace()[1].toString();
            WS(msg);
            return null;
        }
    }

    @VisibleForTesting
    Chapter getChapterFromRemote(String url) {
        String fileName = url.split("/")[2] + ".lua";
        LuaState lua = getLua(mContext);
        if (lua == null) return null;
        try {
            luaSafeDoString(lua, is2String(mContext.getAssets().open(fileName)));
            lua.getField(LuaState.LUA_GLOBALSINDEX, "getChapter");
            lua.pushString(url);
            luaSafeRun(lua, 1, 1);
            return (Chapter) lua.toJavaObject(-1);
        } catch (Exception e) {
            String msg = e + "\n";
            msg += e.getStackTrace()[1].toString();
            WS(msg);
            return null;
        }
    }
}
