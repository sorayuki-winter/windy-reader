package com.wintersky.windyreader.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.AppExecutors;

import org.keplerproject.luajava.LuaState;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.wintersky.windyreader.util.Constants.LT;
import static com.wintersky.windyreader.util.Constants.is2String;
import static com.wintersky.windyreader.util.Constants.luaSafeDoString;
import static com.wintersky.windyreader.util.Constants.luaSafeRun;

@Singleton
public class RemoteDataSource implements DataSource {

    private final AppExecutors mAppExecutors;

    private final Context mContext;

    private final LuaState mLuaState;

    @Inject
    RemoteDataSource(Context context, @NonNull AppExecutors executors, LuaState luaState) {
        mContext = context;
        mAppExecutors = executors;
        mLuaState = luaState;
    }

    /**
     * Note: {@link LoadBooksCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getBooks(final @NonNull LoadBooksCallback callback) {
        callback.onDataNotAvailable();
    }

    @Override
    public void getBook(String bookUrl, GetBookCallback callback) {
        callback.onDataNotAvailable();
    }

    @Override
    public void getChapters(final String bookUrl, final LoadChaptersCallback callback) {
        mAppExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Chapter> list = getChapterListFromRemote(bookUrl);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (list == null) {
                            callback.onDataNotAvailable();
                        } else {
                            callback.onChaptersLoaded(list);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void getChapter(String chapterUrl, GetChapterCallback callback) {

    }

    @VisibleForTesting
    List<Book> searchBookFromRemote(String url, String key) {
        List<Book> list = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=https?://)([^/]*)").matcher(url);
        String fileName = ".lua";
        if (m.find()) fileName = m.group() + fileName;
        try {
            luaSafeDoString(mLuaState, is2String(mContext.getAssets().open(fileName)));
            mLuaState.pushString(url);
            mLuaState.setGlobal("searchUrl");
            mLuaState.pushString(key);
            mLuaState.setGlobal("keyWord");
            mLuaState.pushJavaObject(list);
            mLuaState.setGlobal("list");
            mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "search");
            luaSafeRun(mLuaState);
        } catch (Exception e) {
            String msg = "search book fail\n" + e + "\n";
            msg += e.getStackTrace()[0].toString();
            Log.i(LT, msg);
        }
        return list;
    }

    @VisibleForTesting
    public Book getBookFromRemote(String url) {
        Book book = new Book();
        book.url = url;
        String fileName = url.split("/")[2] + ".lua";
        try {
            luaSafeDoString(mLuaState, is2String(mContext.getAssets().open(fileName)));
            mLuaState.pushString(url);
            mLuaState.setGlobal("bookUrl");
            mLuaState.pushJavaObject(book);
            mLuaState.setGlobal("book");
            mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "getBook");
            luaSafeRun(mLuaState);
        } catch (Exception e) {
            String msg = "get book fail\n" + e + "\n";
            msg += e.getStackTrace()[0].toString();
            Log.i(LT, msg);
        }
        return book;
    }

    @Nullable
    @VisibleForTesting
    List<Chapter> getChapterListFromRemote(String url) {
        List<Chapter> list = new ArrayList<>();
        String fileName = url.split("/")[2] + ".lua";
        try {
            luaSafeDoString(mLuaState, is2String(mContext.getAssets().open(fileName)));
            mLuaState.pushString(url);
            mLuaState.setGlobal("chapterListUrl");
            mLuaState.pushJavaObject(list);
            mLuaState.setGlobal("list");
            mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "getChapterList");
            luaSafeRun(mLuaState);
        } catch (Exception e) {
            String msg = "get chapter list fail\n" + e + "\n";
            msg += e.getStackTrace()[0].toString();
            Log.i(LT, msg);
        }
        return list;
    }

    @VisibleForTesting
    Chapter getChapterFromRemote(String url) {
        Chapter chapter = new Chapter();
        chapter.url = url;
        String fileName = url.split("/")[2] + ".lua";
        mLuaState.setTop(0);
        try {
            luaSafeDoString(mLuaState, is2String(mContext.getAssets().open(fileName)));
            mLuaState.pushString(url);
            mLuaState.setGlobal("chapterUrl");
            mLuaState.pushJavaObject(chapter);
            mLuaState.setGlobal("chapter");
            mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "getChapter");
            luaSafeRun(mLuaState);
        } catch (Exception e) {
            String msg = "get chapter fail\n" + e + "\n";
            msg += e.getStackTrace()[0].toString();
            Log.i(LT, msg);
        }
        return chapter;
    }
}
