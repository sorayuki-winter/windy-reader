package com.wintersky.windyreader.data.source;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.Library;

import java.util.List;

public interface DataSource {

    void getLibraries(LoadLibrariesCallback callback);

    void searchBook(String url, String key, SearchBookCallback callback);

    void getBooks(LoadBooksCallback callback);

    void getBook(String bookUrl, GetBookCallback callback);

    void getChapters(String bookUrl, LoadChaptersCallback callback);

    void getChapter(String chapterUrl, GetChapterCallback callback);

    void saveBook(Book book);

    interface LoadLibrariesCallback {

        void onLibrariesLoaded(List<Library> list);

        void onDataNotAvailable();
    }

    interface SearchBookCallback {

        void onBookSearched(List<Book> books);

        void onDataNotAvailable();
    }

    interface LoadBooksCallback {

        void onBooksLoaded(List<Book> books);

        void onDataNotAvailable();
    }

    interface GetBookCallback {

        void onBookLoaded(Book book);

        void onDataNotAvailable();
    }

    interface LoadChaptersCallback {

        void onChaptersLoaded(List<Chapter> list);

        void onDataNotAvailable();
    }

    interface GetChapterCallback {

        void onChapterLoaded(Chapter chapter);

        void onDataNotAvailable();
    }
}
