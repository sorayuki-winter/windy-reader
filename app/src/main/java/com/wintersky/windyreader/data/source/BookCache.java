package com.wintersky.windyreader.data.source;

import android.os.Looper;
import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.local.LocalDataSource;
import com.wintersky.windyreader.data.source.remote.RemoteDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

@Singleton
public class BookCache {

    private final LocalDataSource mLocal;
    private final RemoteDataSource mRemote;
    private final Thread mThread;

    @Inject
    public BookCache(LocalDataSource local, RemoteDataSource remote) {
        mLocal = local;
        mRemote = remote;

        mThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                final Realm realm = Realm.getDefaultInstance();
                RealmResults<Chapter> chapters = realm.where(Chapter.class).findAll();
                for (Chapter ins : chapters) {
                    if (isInterrupted()) {
                        quit();
                        break;
                    }
                    cacheChapter(ins);
                }
                chapters.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Chapter>>() {
                    @Override
                    public void onChange(@NonNull RealmResults<Chapter> chapters, @NonNull OrderedCollectionChangeSet changeSet) {
                        for (int index : changeSet.getInsertions()) {
                            if (isInterrupted()) {
                                quit();
                                break;
                            }
                            Chapter ins = chapters.get(index);
                            cacheChapter(ins);
                        }
                    }
                });
                Looper.loop();
                realm.close();
            }
        };
        mThread.start();
    }

    private void cacheChapter(Chapter chapter) {
        if (chapter == null) {
            return;
        }
        if (!mLocal.isContentExist(chapter.getUrl())) {
            try {
                String content = mRemote.getContentFrom(chapter.getUrl());
                mLocal.saveContentTo(chapter.getUrl(), content);
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
