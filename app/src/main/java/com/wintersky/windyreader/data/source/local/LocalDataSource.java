/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wintersky.windyreader.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.AppExecutors;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmException;
import lombok.Cleanup;

import static com.wintersky.windyreader.util.LogUtil.LOG;

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class LocalDataSource implements DataSource {

    private final Context mContext;
    private final AppExecutors mExecutors;
    private final Realm mRealm;

    @Inject
    LocalDataSource(@NonNull Context context, @NonNull AppExecutors executors, @NonNull Realm realm) {
        mContext = context;
        mExecutors = executors;
        mRealm = realm;
    }

    @Override
    public void getShelf(@NonNull final GetShelfCallback callback) {
        RealmResults<Book> books = mRealm.where(Book.class).findAll().sort("lastRead", Sort.DESCENDING);
        callback.onLoaded(books);
    }

    @Override
    public void getBook(@NonNull final String url, @NonNull final GetBookCallback callback) {
        Book book = mRealm.where(Book.class).equalTo("url", url).findFirst();
        if (book != null) {
            callback.onLoaded(book);
        } else {
            callback.onFailed(new Exception("book not find: " + url));
        }
    }

    @Override
    public void getCatalog(@NonNull String url, @NonNull GetCatalogCallback callback) {
        Book book = mRealm.where(Book.class).equalTo("catalogUrl", url).findFirst();
        if (book != null) {
            callback.onLoaded(book.getCatalog());
        } else {
            callback.onFailed(new Exception("Catalog not find: " + url));
        }
    }

    @Override
    public void getContent(@NonNull final String url, @NonNull final GetContentCallback callback) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String content = getContentFrom(url);
                    mExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onLoaded(content);
                        }
                    });
                } catch (final IOException e) {
                    mExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(e);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void saveBook(@NonNull final Book book, @NonNull SaveBookCallback callback) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(book);
            }
        });
        callback.onSaved(book);
    }

    @Override
    public void deleteBook(@NonNull String url) {
        final Book book = mRealm.where(Book.class).equalTo("url", url).findFirst();
        if (book != null) {
            final List<String> list = new ArrayList<>();
            for (Chapter chapter : book.catalog.createSnapshot()) {
                list.add(chapter.url);
            }
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    book.getCatalog().deleteAllFromRealm();
                    book.deleteFromRealm();
                }
            });
            mExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    for (String url : list) {
                        try {
                            delContentFrom(url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            LOG(new RealmException("Delete - book not find: " + url));
        }
    }

    public boolean saveContentTo(@NonNull String url, @NonNull String content) {
        File root = mContext.getExternalFilesDir("");
        if (root == null) {
            return false;
        }
        File chapter = new File(root, "chapter");
        if (!chapter.exists() && !chapter.mkdir()) {
            return false;
        }
        File file = new File(chapter, url.replace("/", "_") + ".txt");
        try {
            if (!file.exists() && !file.createNewFile()) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            @Cleanup BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(content.getBytes());
            bos.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            return false;
        }
    }

    public void delContentFrom(@NonNull String url) throws IOException {
        File root = mContext.getExternalFilesDir("");
        if (root == null) {
            throw new IOException("shared storage is not currently available");
        }
        String fileName = url.replace("/", "_") + ".txt";
        File file = new File(root, "chapter/" + fileName);
        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    @NonNull
    public String getContentFrom(@NonNull String url) throws IOException {
        File root = mContext.getExternalFilesDir("");
        if (root == null) {
            throw new IOException("shared storage is not currently available");
        }
        String fileName = url.replace("/", "_") + ".txt";
        File file = new File(root, "chapter/" + fileName);
        if (!file.exists()) {
            throw new IOException(file.getAbsolutePath() + " not exist");
        }
        @Cleanup BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        byte[] buff = new byte[bis.available()];
        //noinspection ResultOfMethodCallIgnored
        bis.read(buff);
        return new String(buff);
    }

    public boolean isContentExist(@NonNull String url) {
        File root = mContext.getExternalFilesDir("");
        if (root == null) {
            return false;
        }
        File file = new File(root, "chapter/" + url.replace("/", "_") + ".txt");
        return file.exists();
    }
}
