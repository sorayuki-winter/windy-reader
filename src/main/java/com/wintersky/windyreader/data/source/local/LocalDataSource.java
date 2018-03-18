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
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.AppExecutors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class LocalDataSource implements DataSource {

    private final static Map<String, Book> BOOKS_LOCAL_DATA = new LinkedHashMap<>(10);

    static {
        Book book = new Book();
        book.url = "http://www.8wenku.com/book/1498";
        book.title = "OVERLORD不死者之王";
        book.chapterListUrl = "http://www.8wenku.com/book/1498";
        book.imgUrl = "http://xs.dmzj.com/img/webpic/28/151117overlordl.jpg";
        book.author = "";
        book.status = "";
        book.classify = "";
        book.detail = "一款席卷游戏界的网路游戏「YGGDRASIL」，有一天突然毫无预警地停止一切服务——原本应该是如此。但是不知为何它却成了一款即使过了结束时间，玩家角色依然不会登出的游戏。NPC开始拥有自己的思想。 现实世界当中一名喜欢电玩的普通青年，似乎和整个公会一起穿越到异世界，变成拥有骷髅外表的最强魔法师「飞鼠」。他率领的公会「安兹．乌尔．恭」将展开前所未有的奇幻传说！";
        BOOKS_LOCAL_DATA.put(book.url, book);
    }

    private final AppExecutors mAppExecutors;

    @Inject
    LocalDataSource(@NonNull AppExecutors executors) {
        mAppExecutors = executors;
    }

    @Override
    public void getBooks(@NonNull LoadBooksCallback callback) {
        callback.onBooksLoaded(new ArrayList<Book>(BOOKS_LOCAL_DATA.values()));
    }

    @Override
    public void getBook(String bookUrl, GetBookCallback callback) {
        Book book = BOOKS_LOCAL_DATA.get(bookUrl);

        if (book == null) {
            callback.onDataNotAvailable();
        } else {
            callback.onBookLoaded(book);
        }
    }

    @Override
    public void getChapters(String bookUrl, LoadChaptersCallback callback) {
        callback.onDataNotAvailable();
    }

    @Override
    public void getChapter(String chapterUrl, GetChapterCallback callback) {
        callback.onDataNotAvailable();
    }
}
