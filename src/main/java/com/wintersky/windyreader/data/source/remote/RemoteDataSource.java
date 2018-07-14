package com.wintersky.windyreader.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.AppExecutors;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.keplerproject.luajava.LuaState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final LuaState mLua;

    @Inject
    RemoteDataSource(Context context, @NonNull AppExecutors executors, LuaState lua) {
        mContext = context;
        mExecutors = executors;
        mLua = lua;
    }

    private Future taskCatalog;

    @Override
    public void searchBook(final String url, final String key, final SearchBookCallback callback) {
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Book> list = searchBookFromRemote(url, key);
                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (list == null)
                            callback.onDataNotAvailable();
                        else callback.onSearched(list);
                    }
                });
            }
        });
    }

    private Future taskChapter;

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

    @VisibleForTesting
    List<Book> searchBookFromRemote(String url, String key) {
        List<Book> list = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=https?://)([^/]*)").matcher(url);
        String fileName = ".lua";
        if (m.find()) fileName = m.group() + fileName;
        mLua.setTop(1);
        try {
            luaSafeDoString(mLua, is2String(mContext.getAssets().open(fileName)));
            mLua.getField(LuaState.LUA_GLOBALSINDEX, "search");
            mLua.pushString(url);
            mLua.pushString(key);
            mLua.pushJavaObject(list);
            luaSafeRun(mLua, 3, 0);
        } catch (Exception e) {
            String msg = "search book fail\n" + e + "\n";
            msg += e.getStackTrace()[0].toString();
            WS(msg);
            return null;
        }
        return list;
    }

    @VisibleForTesting
    Book getBookFromRemote(String url) {
        Book book = new Book();
        book.setUrl(url);
        String fileName = url.split("/")[2] + ".lua";
        mLua.setTop(1);
        try {
            luaSafeDoString(mLua, is2String(mContext.getAssets().open(fileName)));
            mLua.getField(LuaState.LUA_GLOBALSINDEX, "getBook");
            mLua.pushString(url);
            mLua.pushJavaObject(book);
            luaSafeRun(mLua, 2, 0);
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
        mLua.setTop(1);
        try {
            luaSafeDoString(mLua, is2String(mContext.getAssets().open(fileName)));
            mLua.getField(LuaState.LUA_GLOBALSINDEX, "getCatalog");
            mLua.pushString(url);
            luaSafeRun(mLua, 1, 1);
            Elements list = (Elements) mLua.toJavaObject(-1);
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
        mLua.setTop(1);
        try {
            luaSafeDoString(mLua, is2String(mContext.getAssets().open(fileName)));
            mLua.getField(LuaState.LUA_GLOBALSINDEX, "getChapter");
            mLua.pushString(url);
            luaSafeRun(mLua, 1, 1);
            return (Chapter) mLua.toJavaObject(-1);
        } catch (Exception e) {
            String msg = e + "\n";
            msg += e.getStackTrace()[1].toString();
            WS(msg);
            return null;
        }
    }
}
