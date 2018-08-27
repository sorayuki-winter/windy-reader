package com.wintersky.windyreader.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.AppExecutors;

import org.keplerproject.luajava.LuaState;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.RealmList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.wintersky.windyreader.util.LuaTools.getLua;
import static com.wintersky.windyreader.util.LuaTools.is2String;
import static com.wintersky.windyreader.util.LuaTools.luaSafeDoString;
import static com.wintersky.windyreader.util.LuaTools.luaSafeRun;

@Singleton
public class RemoteDataSource implements DataSource, DataSource.Remote {

    private final Context mContext;
    private final AppExecutors mExecutors;
    private final OkHttpClient mHttp;

    private Future taskCatalog;
    private Future taskChapter;

    @Inject
    RemoteDataSource(Context context, @NonNull AppExecutors executors, OkHttpClient http) {
        mContext = context;
        mExecutors = executors;
        mHttp = http;
    }

    @Override
    public void getBook(final String url, final GetBookCallback callback) {
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Book book = getBookFrom(url);
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
    public void getCatalog(final String url, final GetCatalogCallback callback) {
        if (taskCatalog != null) {
            taskCatalog.cancel(true);
        }
        taskCatalog = mExecutors.networkIO().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Chapter> list = getCatalogFrom(url);
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
                    final Chapter chapter = getChapterFrom(url);
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


    public Book getBookFrom(String url) throws Exception {
        String fileName = url.split("/")[2].replace('.', '_') + ".lua";
        LuaState lua = getLua(mContext);

        Request request = new Request.Builder().url(url).build();
        Response response = mHttp.newCall(request).execute();
        byte[] bytes = Objects.requireNonNull(response.body()).bytes();
        String doc = new String(bytes, "UTF-8");
        if (doc.contains("charset=gbk")) {
            doc = new String(bytes, "GBK");
        }

        luaSafeDoString(lua, is2String(mContext.getAssets().open(fileName)), 1);
        lua.getField(-1, "getBook");
        lua.pushString(url);
        lua.pushString(doc);
        luaSafeRun(lua, 2, 1);
        String res = lua.toString(-1);
        try {
            return new Gson().fromJson(res, Book.class);
        } catch (Exception e) {
            throw formatJsonError(url, res, e);
        }
    }


    public RealmList<Chapter> getCatalogFrom(String url) throws Exception {
        String fileName = url.split("/")[2].replace('.', '_') + ".lua";
        LuaState lua = getLua(mContext);

        Request request = new Request.Builder().url(url).build();
        Response response = mHttp.newCall(request).execute();
        byte[] bytes = Objects.requireNonNull(response.body()).bytes();
        String doc = new String(bytes, "UTF-8");
        if (doc.contains("charset=gbk")) {
            doc = new String(bytes, "GBK");
        }

        luaSafeDoString(lua, is2String(mContext.getAssets().open(fileName)), 1);
        lua.getField(-1, "getCatalog");
        lua.pushString(url);
        lua.pushString(doc);
        luaSafeRun(lua, 2, 1);
        String res = lua.toString(-1);
        try {
            return new Gson().fromJson(res, new TypeToken<RealmList<Chapter>>() {
            }.getType());
        } catch (Exception e) {
            throw formatJsonError(url, res, e);
        }
    }


    public Chapter getChapterFrom(String url) throws Exception {
        String fileName = url.split("/")[2].replace('.', '_') + ".lua";
        LuaState lua = getLua(mContext);

        Request request = new Request.Builder().url(url).build();
        Response response = mHttp.newCall(request).execute();
        byte[] bytes = Objects.requireNonNull(response.body()).bytes();
        String doc = new String(bytes, "UTF-8");
        if (doc.contains("charset=gbk")) {
            doc = new String(bytes, "GBK");
        }

        luaSafeDoString(lua, is2String(mContext.getAssets().open(fileName)), 1);
        lua.getField(-1, "getChapter");
        lua.pushString(url);
        lua.pushString(doc);
        luaSafeRun(lua, 2, 1);
        String res = lua.toString(-1);
        try {
            return new Gson().fromJson(res, Chapter.class);
        } catch (Exception e) {
            throw formatJsonError(url, res, e);
        }
    }

    private Exception formatJsonError(String url, String json, Exception e) {
        Matcher matcher = Pattern.compile("at line (\\d+) column (\\d+) path \\$\\.(\\w+)").matcher(e.getMessage());
        if (matcher.find()) {
            int line = Integer.valueOf(matcher.group(1));
            String error = json.split("\n")[line - 1];
            Exception exception = new Exception(e.getMessage());
            StackTraceElement[] elements = e.getStackTrace();
            StackTraceElement element = elements[0];
            element = new StackTraceElement(
                    String.format("%s\nurl: %s\nat %s", error, url, element.getClassName()),
                    element.getMethodName(), element.getFileName(), element.getLineNumber());
            elements[0] = element;
            exception.setStackTrace(elements);
            return exception;
        }
        return e;
    }
}
