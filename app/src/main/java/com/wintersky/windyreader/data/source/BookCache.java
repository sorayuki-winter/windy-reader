package com.wintersky.windyreader.data.source;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.local.LocalDataSource;
import com.wintersky.windyreader.data.source.remote.RemoteDataSource;
import com.wintersky.windyreader.util.FileUtil;

import org.keplerproject.luajava.LuaException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import lombok.Cleanup;

import static com.wintersky.windyreader.data.source.local.LocalDataSource.CACHE_DIR;
import static com.wintersky.windyreader.util.LogUtil.LOGD;

@Singleton
public class BookCache implements Runnable {

    private final Context mContext;
    private final LocalDataSource mLocal;
    private final RemoteDataSource mRemote;
    private final ThreadPoolExecutor mExecutor;
    private Thread mThread;
    private Handler mHandler;

    @Inject
    BookCache(final Context context, LocalDataSource local, RemoteDataSource remote) {
        mContext = context;
        mLocal = local;
        mRemote = remote;
        int coreNum = Runtime.getRuntime().availableProcessors();
        mExecutor = new ThreadPoolExecutor(coreNum, coreNum,
                                           1, TimeUnit.SECONDS,
                                           new LinkedBlockingQueue<Runnable>());
        mExecutor.allowCoreThreadTimeOut(true);
    }

    public void cache() {
        if (mThread != null && mThread.isAlive() && !mThread.isInterrupted()) {
            return;
        }
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void run() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        mHandler = new Handler();
        @Cleanup Realm realm = Realm.getDefaultInstance();

        RealmResults<Book> books = realm.where(Book.class).findAll();
        List<String> bkDirs = new ArrayList<>();
        List<String> ctUrls = new ArrayList<>();
        for (Book book : books) {
            bkDirs.add(book.getCatalogUrl().replace("/", "_"));
            ctUrls.add(book.getCatalogUrl());
        }
        // noinspection all
        books = null;

        File root = mContext.getExternalFilesDir("");
        if (root != null) {
            File cacheDir = new File(root, CACHE_DIR);
            if (cacheDir.exists()) {
                for (File file : cacheDir.listFiles()) {
                    if (file.isFile() && !file.delete()) {
                        LOGD("File delete fail: " + file.getAbsolutePath());
                    }
                    if (file.isDirectory() && !bkDirs.contains(file.getName())) {
                        FileUtil.delete(file);
                    }
                }
            }
        }
        bkDirs.clear();
        // noinspection all
        bkDirs = null;

        RealmResults<Chapter> chapters = realm.where(Chapter.class).findAll();
        for (final Chapter chapter : chapters) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            if (ctUrls.contains(chapter.getCatalogUrl())) {
                cacheChapter(chapter);
            } else {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(@NonNull final Realm realm) {
                        chapter.deleteFromRealm();
                    }
                });
            }
        }
        ctUrls.clear();
        // noinspection all
        ctUrls = null;

        chapters.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Chapter>>() {
            @Override
            public void onChange(@NonNull RealmResults<Chapter> chapters, @NonNull OrderedCollectionChangeSet changeSet) {
                for (int index : changeSet.getInsertions()) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    Chapter chapter = chapters.get(index);
                    cacheChapter(chapter);
                }
            }
        });
        Looper.loop();
    }

    private void cacheChapter(Chapter chapter) {
        if (chapter == null) {
            return;
        }
        final String ctUrl = chapter.getCatalogUrl();
        final String chUrl = chapter.getUrl();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (!mLocal.isContentExist(ctUrl, chUrl)) {
                    try {
                        String content = mRemote.getContentFrom(chUrl);
                        mLocal.saveContentTo(ctUrl, chUrl, content);
                    } catch (LuaException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void close() {
        if (mThread == null || !mThread.isAlive() || mThread.isInterrupted()) {
            return;
        }
        mThread.interrupt();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Looper looper = Looper.myLooper();
                if (looper != null) {
                    looper.quitSafely();
                }
            }
        });
        mExecutor.getQueue().clear();
    }
}
