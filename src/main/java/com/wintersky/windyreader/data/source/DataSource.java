package com.wintersky.windyreader.data.source;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.Library;

import java.util.List;

import io.realm.RealmResults;

public interface DataSource {

    void getLList(LoadLListCallback callback);

    void searchBook(String url, String key, SearchBookCallback callback);

    void getBList(LoadBListCallback callback);

    void getBook(String url, GetBookCallback callback);

    void getChapter(String url, GetChapterCallback callback);

    void saveBook(Book book);

    void updateCheck(String url, UpdateCheckCallback callback);

    interface LoadLListCallback {

        void onLoaded(List<Library> list);

        void onDataNotAvailable();
    }

    interface SearchBookCallback {

        void onSearched(List<Book> list);

        void onDataNotAvailable();
    }

    interface LoadBListCallback {

        void onLoaded(RealmResults<Book> list);

        void onDataNotAvailable();
    }

    interface GetBookCallback {

        void onLoaded(Book book);

        void onDataNotAvailable();
    }

    interface UpdateCheckCallback {

        void onChecked();

        void onDataNotAvailable();
    }

    interface GetChapterCallback {

        void onLoaded(Chapter chapter);

        void onDataNotAvailable();
    }
}
