package com.wintersky.windyreader.data.source;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.Library;

import java.util.List;

public interface DataSource {

    void getLList(LoadLListCallback callback);

    void searchBook(String url, String key, SearchBookCallback callback);

    void getBList(LoadBListCallback callback);

    void getBook(String url, GetBookCallback callback);

    void getCList(String url, LoadCListCallback callback);

    void getChapter(String url, GetChapterCallback callback);

    void saveBook(Book book);

    interface LoadLListCallback {

        void onLoaded(List<Library> list);

        void onDataNotAvailable();
    }

    interface SearchBookCallback {

        void onSearched(List<Book> list);

        void onDataNotAvailable();
    }

    interface LoadBListCallback {

        void onLoaded(List<Book> list);

        void onDataNotAvailable();
    }

    interface GetBookCallback {

        void onLoaded(Book book);

        void onDataNotAvailable();
    }

    interface LoadCListCallback {

        void onLoading(Chapter chapter);

        void onLoaded();

        void onDataNotAvailable();
    }

    interface GetChapterCallback {

        void onLoaded(Chapter chapter);

        void onDataNotAvailable();
    }
}
