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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;

@Singleton
public class UpdateCheck {

    private final Context mContext;
    private final RemoteDataSource mRemote;
    private final ScheduledExecutorService mService;

    @Inject
    UpdateCheck(Context context, RemoteDataSource remote) {
        mContext = context;
        mRemote = remote;

        mService = Executors.newSingleThreadScheduledExecutor();
        mService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<Book> results = realm.where(Book.class).findAll();
                for (final Book book : results.createSnapshot()) {
                    try {
                        final List<Chapter> list = mRemote.getCatalogFrom(book.getCatalogUrl());
                        final List<Chapter> catalog = book.getCatalog();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                if (catalog.size() < list.size()) {
                                    book.setHasNew(true);
                                    book.setLastRead(new Date());
                                    catalog.addAll(list.subList(catalog.size(), list.size()));
                                    Toast.makeText(mContext, "New Chapter:\n" + book.title, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } catch (LuaException | IOException e) {
                        e.printStackTrace();
                    }
                }
                realm.close();
            }
        }, 0, 10, TimeUnit.MINUTES);
    }

    public void close() {
        mService.shutdown();
    }
}
