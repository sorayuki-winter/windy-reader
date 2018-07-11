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

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Library;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class LocalDataSource implements DataSource {

    private final AppExecutors mExecutors;

    private final SharedPreferences mSP;

    @Inject
    LocalDataSource(@NonNull AppExecutors executors, SharedPreferences sp) {
        mExecutors = executors;
        mSP = sp;
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
        Book book = new Book();
        book.setUrl("http://zxzw.com/164588/");
        book.setTitle("合体双修");
        book.setCurrentCUrl(mSP.getString("currentCUrl", ""));
        if (!book.getCurrentCUrl().startsWith("http://")) {
            book.setCurrentCUrl("http://zxzw.com/164588/14192209/");
        }
        List<Book> bks = new ArrayList<>();
        bks.add(book);
        callback.onBooksLoaded(bks);
    }

    @Override
    public void getBook(final String bookUrl, final GetBookCallback callback) {
        Book book = new Book();
        book.setUrl("http://zxzw.com/164588/");
        book.setTitle("合体双修");
        book.setCurrentCUrl(mSP.getString("currentCUrl", ""));
        if (!book.getCurrentCUrl().startsWith("http://")) {
            book.setCurrentCUrl("http://zxzw.com/164588/14192209/");
        }
        callback.onBookLoaded(book);
    }

    @Override
    public void getChapters(final String bookUrl, final LoadChaptersCallback callback) {
        callback.onDataNotAvailable();
    }

    @Override
    public void getChapter(String chapterUrl, GetChapterCallback callback) {
        callback.onDataNotAvailable();
    }

    @Override
    public void saveBook(final Book book) {
        mSP.edit().putString("currentCUrl", book.getCurrentCUrl()).apply();
    }
}
