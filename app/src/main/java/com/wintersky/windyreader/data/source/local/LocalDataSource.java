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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.wintersky.windyreader.util.LogTools.LOG;

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class LocalDataSource implements DataSource, DataSource.Local {

    private final Context mContext;
    private final AppExecutors mExecutors;
    private final Realm mRealm;

    @Inject
    LocalDataSource(Context context, @NonNull AppExecutors executors, Realm realm) {
        mContext = context;
        mExecutors = executors;
        mRealm = realm;
    }

    @Override
    public void getShelf(@NonNull final GetShelfCallback callback) {
        RealmResults<Book> books = mRealm.where(Book.class).findAll();
        callback.onLoaded(books);
    }

    @Override
    public void getBook(final String url, final GetBookCallback callback) {
        Book book = mRealm.where(Book.class).equalTo("url", url).findFirst();
        if (book != null) {
            callback.onLoaded(book);
        } else {
            callback.onDataNotAvailable(new Exception("book not find: " + url));
        }
    }

    @Override
    public void getCatalog(String url, GetCatalogCallback callback) {
        Book book = mRealm.where(Book.class).equalTo("url", url).findFirst();
        if (book != null) {
            callback.onLoaded(book.getCatalog());
        } else {
            callback.onDataNotAvailable(new Exception("Book not find: " + url));
        }
    }

    @Override
    public void getChapter(final String url, final GetChapterCallback callback) {
        final Chapter chapter = mRealm.where(Chapter.class).equalTo("url", url).findFirst();
        if (chapter != null) {
            mExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    String content = null;
                    try {
                        content = getContentFrom(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chapter.setContent(content);
                    mExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onLoaded(chapter);
                        }
                    });
                }
            });
        } else {
            callback.onDataNotAvailable(new Exception("chapter not find: " + url));
        }
    }

    @Override
    public void saveBook(final Book book) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(book);
            }
        });
    }

    @Override
    public void deleteBook(String url) {
        getBook(url, new GetBookCallback() {
            @Override
            public void onLoaded(final Book book) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(@NonNull Realm realm) {
                        book.deleteFromRealm();
                    }
                });
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(bs));
                LOG("delete book fail", bs.toString());
            }
        });
    }

    @Override
    public void cacheChapter(final Chapter chapter) {
        final String url = chapter.getUrl();
        final String content = chapter.getContent();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                saveContentTo(url, content);
            }
        });
    }


    public boolean saveContentTo(String url, String content) {
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
        } catch (Exception e) {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(bo));
            LOG(bo.toString());
            return false;
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(content.getBytes());
            bos.flush();
        } catch (Exception e) {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(bo));
            LOG(bo.toString());
            file.delete();
            return false;
        }
        return true;
    }


    public String getContentFrom(String url) throws Exception {
        File root = mContext.getExternalFilesDir("");
        if (root == null) {
            return null;
        }
        File chapter = new File(root, "chapter");
        if (chapter.exists() || chapter.mkdir()) {
            File file = new File(chapter, url.replace("/", "_") + ".txt");
            if (file.exists()) {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                byte[] buff = new byte[bis.available()];
                bis.read(buff);
                return new String(buff);
            }
        }
        return null;
    }


    public boolean isContentExist(String url) {
        File root = mContext.getExternalFilesDir("");
        if (root == null) return false;
        File file = new File(root, "chapter/" + url.replace("/", "_") + ".txt");
        return file.exists();
    }
}
