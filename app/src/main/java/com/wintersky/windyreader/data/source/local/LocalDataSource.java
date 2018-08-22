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
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.AppExecutors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class LocalDataSource implements DataSource {

    private final AppExecutors mExecutors;
    private final Realm mRealm;

    @Inject
    LocalDataSource(@NonNull AppExecutors executors, Realm realm) {
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
            callback.onDataNotAvailable(new Exception());
        }
    }

    @Override
    public void getCatalog(String url, GetCatalogCallback callback) {
        // none
    }

    @Override
    public void getChapter(String url, final GetChapterCallback callback) {
        Chapter chapter = mRealm.where(Chapter.class).equalTo("url", url).findFirst();
        if (chapter != null) {
            callback.onLoaded(chapter);
        } else {
            callback.onDataNotAvailable(new Exception());
        }
    }

    @Override
    public void saveBook(final Book book) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(book);
        mRealm.commitTransaction();
    }

    @Override
    public void updateCheck(String url, UpdateCheckCallback callback) {
        // none
    }
}
