package com.wintersky.windyreader.data.source;

import android.os.AsyncTask;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource.CacheBookCallback;
import com.wintersky.windyreader.data.source.local.LocalDataSource;
import com.wintersky.windyreader.data.source.remote.RemoteDataSource;

import java.util.List;

import io.realm.Realm;

import static com.wintersky.windyreader.util.LogUtil.LOG;

public class CacheBookTask extends AsyncTask<String, Void, Void> {

    private LocalDataSource mLocal;
    private RemoteDataSource mRemote;
    private CacheBookCallback mCallback;

    CacheBookTask(LocalDataSource localDataSource, RemoteDataSource remoteDataSource, DataSource.CacheBookCallback callback) {
        mLocal = localDataSource;
        mRemote = remoteDataSource;
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String bookUrl = strings[0];

        Realm realm = Realm.getDefaultInstance();
        Book book = realm.where(Book.class).equalTo("url", bookUrl).findFirst();
        if (book != null) {
            List<Chapter> catalog = book.getCatalog();
            for (int i = 0; i < catalog.size(); i++) {
                if (isCancelled()) {
                    break;
                }
                final Chapter c = catalog.get(i);
                if (c == null) {
                    continue;
                }
                if (!mLocal.isContentExist(c.getUrl())) {
                    try {
                        String content = mRemote.getContentFrom(c.getUrl());
                        mLocal.saveContentTo(c.getUrl(), content);
                    } catch (Exception e) {
                        LOG(e);
                    }
                }
            }
        } else {
            LOG(new Exception("book not find: " + bookUrl));
        }
        realm.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mCallback.onCached();
    }
}