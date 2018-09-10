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
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmException;

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class LocalDataSource implements DataSource {

    public static String CACHE_DIR = "BookCache/";
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
        RealmResults<Book> books = mRealm.where(Book.class).sort("lastRead", Sort.DESCENDING).findAllAsync();
        callback.onLoaded(books);
    }

    @Override
    public void getBook(@NonNull final String bkUrl, @NonNull final GetBookCallback callback) {
        final Book book = mRealm.where(Book.class).equalTo("url", bkUrl).findFirstAsync();
        book.addChangeListener(new RealmObjectChangeListener<RealmModel>() {
            @Override
            public void onChange(@NonNull final RealmModel model, @Nullable final ObjectChangeSet changeSet) {
                if (book.isValid()) {
                    callback.onLoaded(book);
                } else {
                    callback.onFailed(new RealmException("Book not find: " + bkUrl));
                }
                book.removeAllChangeListeners();
            }
        });
    }

    @Override
    public void getCatalog(@NonNull final Book book, @NonNull final GetCatalogCallback callback) {
        final Book bk = mRealm.where(Book.class).equalTo("catalogUrl", book.getCatalogUrl()).findFirstAsync();
        bk.addChangeListener(new RealmObjectChangeListener<RealmModel>() {
            @Override
            public void onChange(@NonNull final RealmModel model, @Nullable final ObjectChangeSet changeSet) {
                if (bk.isValid()) {
                    callback.onLoaded(bk.getCatalog());
                } else {
                    callback.onFailed(new RealmException("Book not find: (catalogUrl) " + book.getCatalogUrl()));
                }
                bk.removeAllChangeListeners();
            }
        });
    }

    @Override
    public void getContent(@NonNull final Chapter chapter, @NonNull final GetContentCallback callback) {
        final String ctUrl = chapter.getCatalogUrl();
        final String chUrl = chapter.getUrl();
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final String content = getContentFrom(ctUrl, chUrl);
                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (content != null) {
                            callback.onLoaded(content);
                        } else {
                            callback.onFailed(new IOException("Content get fail: " + chUrl));
                        }
                    }
                });
            }
        });
    }

    @Override
    public void saveBook(@NonNull final Book book, @NonNull final SaveBookCallback callback) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(book);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                callback.onSaved(book);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull final Throwable error) {
                callback.onFailed(error);
            }
        });
    }

    @Override
    public void deleteBook(@NonNull String bkUrl, @NonNull final DeleteBookCallback callback) {
        getBook(bkUrl, new GetBookCallback() {
            @Override
            public void onLoaded(@NonNull final Book book) {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(@NonNull final Realm realm) {
                        book.getCatalog().deleteAllFromRealm();
                        book.deleteFromRealm();
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        callback.onDeleted();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(@NonNull final Throwable error) {
                        callback.onFailed(error);
                    }
                });
            }

            @Override
            public void onFailed(@NonNull final Throwable error) {
                callback.onFailed(error);
            }
        });
    }

    public boolean saveContentTo(@NonNull String ctUrl, @NonNull String chUrl, @NonNull String content) {
        File root = mContext.getExternalFilesDir("");
        if (root == null) {
            return false;
        }
        File bkDir = new File(root, CACHE_DIR + ctUrl.replace("/", "_"));
        bkDir.mkdirs();
        File file = new File(bkDir, chUrl.replace("/", "_"));
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos)
        ) {
            bos.write(content.getBytes());
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
            return false;
        }
        return true;
    }

    public String getContentFrom(@NonNull String ctUrl, @NonNull String chUrl) {
        File root = mContext.getExternalFilesDir("");
        if (root == null) {
            return null;
        }
        String fileName = CACHE_DIR + ctUrl.replace("/", "_") + "/" + chUrl.replace("/", "_");
        File file = new File(root, fileName);
        try (
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis)
        ) {
            byte[] buff = new byte[bis.available()];
            bis.read(buff);
            return new String(buff);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isContentExist(@NonNull String ctUrl, @NonNull String chUrl) {
        File root = mContext.getExternalFilesDir("");
        if (root == null) {
            return false;
        }
        String fileName = CACHE_DIR + ctUrl.replace("/", "_") + "/" + chUrl.replace("/", "_");
        File file = new File(root, fileName);
        return file.exists();
    }
}
