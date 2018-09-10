package com.wintersky.windyreader.data.source;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.local.LocalDataSource;
import com.wintersky.windyreader.data.source.remote.RemoteDataSource;
import com.wintersky.windyreader.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.wintersky.windyreader.data.source.local.LocalDataSource.CACHE_DIR;
import static com.wintersky.windyreader.util.LogUtil.LOGD;

@Singleton
public class BookCache implements Runnable {

    private final Context mContext;
    private final LocalDataSource mLocal;
    private final RemoteDataSource mRemote;
    private Thread mThread;

    @Inject
    BookCache(final Context context, LocalDataSource local, RemoteDataSource remote) {
        mContext = context;
        mLocal = local;
        mRemote = remote;
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
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<Book> books = realm.where(Book.class).findAll();
        RealmResults<Chapter> chapters = realm.where(Chapter.class).findAll();

        List<String> bkDirs = new ArrayList<>();
        List<String> ctUrls = new ArrayList<>();
        for (Book book : books) {
            bkDirs.add(book.getCatalogUrl().replace("/", "_"));
            ctUrls.add(book.getCatalogUrl());
        }

        File root = mContext.getExternalFilesDir("");
        if (root != null) {
            File cacheDir = new File(root, CACHE_DIR);
            cacheDir.mkdirs();
            for (File file : cacheDir.listFiles()) {
                if (file.isFile() && !file.delete()) {
                    LOGD("File delete fail: " + file.getAbsolutePath());
                }
                if (file.isDirectory() && !bkDirs.contains(file.getName())) {
                    FileUtil.delete(file);
                }
            }
        }

        for (final Chapter chapter : chapters) {
            if (Thread.currentThread().isInterrupted()) {
                quit();
                break;
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

        chapters.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Chapter>>() {
            @Override
            public void onChange(@NonNull RealmResults<Chapter> chapters, @NonNull OrderedCollectionChangeSet changeSet) {
                for (int index : changeSet.getInsertions()) {
                    if (Thread.currentThread().isInterrupted()) {
                        quit();
                        break;
                    }
                    Chapter chapter = chapters.get(index);
                    cacheChapter(chapter);
                }
            }
        });
        Looper.loop();
        realm.close();
    }

    private void cacheChapter(Chapter chapter) {
        if (chapter == null) {
            return;
        }
        if (!mLocal.isContentExist(chapter.getCatalogUrl(), chapter.getUrl())) {
            try {
                String content = mRemote.getContentFrom(chapter.getUrl());
                mLocal.saveContentTo(chapter.getCatalogUrl(), chapter.getUrl(), content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void quit() {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            looper.quitSafely();
        }
    }

    public void close() {
        mThread.interrupt();
    }
}
