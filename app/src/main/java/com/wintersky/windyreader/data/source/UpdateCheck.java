package com.wintersky.windyreader.data.source;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.remote.RemoteDataSource;

import org.keplerproject.luajava.LuaException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import lombok.Cleanup;

@Singleton
public class UpdateCheck implements Runnable {

    private final Context mContext;
    private final RemoteDataSource mRemote;
    private final ScheduledThreadPoolExecutor mSchedule;
    private final ThreadPoolExecutor mExecutor;
    private ScheduledFuture mFuture;

    @Inject
    UpdateCheck(Context context, RemoteDataSource remote) {
        mContext = context;
        mRemote = remote;
        mSchedule = new ScheduledThreadPoolExecutor(1);
        int coreNum = Runtime.getRuntime().availableProcessors();
        mExecutor = new ThreadPoolExecutor(coreNum, coreNum,
                                           1, TimeUnit.SECONDS,
                                           new LinkedBlockingQueue<Runnable>());
        mExecutor.allowCoreThreadTimeOut(true);
    }

    public void check() {
        if (mFuture != null && !mFuture.isCancelled()) {
            return;
        }
        mFuture = mSchedule.scheduleWithFixedDelay(this, 0, 10, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        @Cleanup Realm realm = Realm.getDefaultInstance();
        RealmResults<Book> books = realm.where(Book.class).findAll();
        for (Book book : books) {
            if (mFuture.isCancelled()) {
                return;
            }
            final String bkUrl = book.getUrl();
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    @Cleanup Realm rm = Realm.getDefaultInstance();
                    final Book bk = rm.where(Book.class).equalTo("url", bkUrl).findFirst();
                    if (bk != null) {
                        try {
                            final List<Chapter> catalogR = mRemote.getCatalogFrom(bk.getCatalogUrl());
                            final List<Chapter> catalogL = bk.getCatalog();
                            rm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(@NonNull Realm realm) {
                                    if (catalogL.size() < catalogR.size()) {
                                        bk.setHasNew(true);
                                        bk.setLastRead(new Date());
                                        catalogL.addAll(catalogR.subList(catalogL.size(), catalogR.size()));
                                        Toast.makeText(mContext, "New Chapter:\n" + bk.getTitle(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } catch (LuaException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void close() {
        if (mFuture == null || mFuture.isDone() || mFuture.isCancelled()) {
            return;
        }
        mFuture.cancel(false);
        mExecutor.getQueue().clear();
    }
}
