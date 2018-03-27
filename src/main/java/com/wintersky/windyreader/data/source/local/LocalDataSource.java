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

import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.Library;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class LocalDataSource implements DataSource {

    private Realm mRealm;

    private final AppExecutors mAppExecutors;

    @Inject
    LocalDataSource(@NonNull AppExecutors executors) {
        mAppExecutors = executors;
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mRealm = Realm.getDefaultInstance();
            }
        });
    }

    @Override
    public void getLibraries(LoadLibrariesCallback callback) {
        List<Library> list = new ArrayList<>();

        Library library = new Library();
        library.name = "八号文库";
        library.baseUrl = "http://www.8wenku.com";
        library.path = "";

        list.add(library);

        callback.onLibrariesLoaded(list);
    }

    @Override
    public void searchBook(String url, String key, SearchBookCallback callback) {
        //none
    }

    @Override
    public void getBooks(@NonNull final LoadBooksCallback callback) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                RealmResults<Book> results = mRealm.where(Book.class).findAll();
                results.sort("lastTime", Sort.DESCENDING);
                final List<Book> books = mRealm.copyFromRealm(results);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onBooksLoaded(books);
                    }
                });
            }
        });
    }

    @Override
    public void getBook(final String bookUrl, final GetBookCallback callback) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Book bk = mRealm.where(Book.class)
                        .equalTo("url", bookUrl).findFirst();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (bk == null)
                            callback.onDataNotAvailable();
                        else
                            callback.onBookLoaded(bk);
                    }
                });
            }
        });
    }

    @Override
    public void getChapters(final String bookUrl, final LoadChaptersCallback callback) {

    }

    @Override
    public void getChapter(String chapterUrl, GetChapterCallback callback) {

    }

    @Override
    public void saveBook(final Book book) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(book);
                mRealm.commitTransaction();
            }
        });
    }
}
