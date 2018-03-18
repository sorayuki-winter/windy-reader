package com.wintersky.windyreader.data.source;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import java.util.List;

public interface DataSource {
    void getBooks(LoadBooksCallback callback);

    void getBook(String bookUrl, GetBookCallback callback);

    void getChapters(String bookUrl, LoadChaptersCallback callback);

    void getChapter(String chapterUrl, GetChapterCallback callback);

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
