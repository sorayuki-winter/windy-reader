package com.wintersky.windyreader.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Repository implements DataSource {

    private final DataSource mLocalDataSource;

    private final DataSource mRemoteDataSource;

    private Map<String, Book> mCachedBooks;

    private boolean mCacheIsDirty = false;

    @Inject
    Repository(@Remote DataSource remoteDataSource, @Local DataSource localDataSource) {
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
    }

    @Override
    public void getLibraries(LoadLibrariesCallback callback) {
        mLocalDataSource.getLibraries(callback);
    }

    @Override
    public void searchBook(String url, String key, SearchBookCallback callback) {
        mRemoteDataSource.searchBook(url, key, callback);
    }

    @Override
    public void getBooks(@NonNull final LoadBooksCallback callback) {
        if (mCachedBooks != null && !mCacheIsDirty) {
            callback.onBooksLoaded(new ArrayList<>(mCachedBooks.values()));
            return;
        }
        getBooksFromLocalDataSource(callback);
    }

    @Override
    public void getBook(String bookUrl, final GetBookCallback callback) {
        Book cachedBook = mCachedBooks.get(bookUrl);

        // Respond immediately with cache if available
        if (cachedBook != null) {
            callback.onBookLoaded(cachedBook);
            return;
        }

        mRemoteDataSource.getBook(bookUrl, new GetBookCallback() {
            @Override
            public void onBookLoaded(Book book) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedBooks == null) {
                    mCachedBooks = new LinkedHashMap<>();
                }
                mCachedBooks.put(book.url, book);
                callback.onBookLoaded(book);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getChapters(final String bookUrl, final LoadChaptersCallback callback) {
        mRemoteDataSource.getChapters(bookUrl, new LoadChaptersCallback() {
            @Override
            public void onChaptersLoaded(List<Chapter> list) {
                callback.onChaptersLoaded(list);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getChapter(final String chapterUrl, final GetChapterCallback callback) {
        mLocalDataSource.getChapter(chapterUrl, new GetChapterCallback() {
            @Override
            public void onChapterLoaded(Chapter chapter) {
                if(chapter.content != null)
                    callback.onChapterLoaded(chapter);
                else
                    mRemoteDataSource.getChapter(chapterUrl, callback);
            }

            @Override
            public void onDataNotAvailable() {
                mRemoteDataSource.getChapter(chapterUrl, callback);
            }
        });
    }

    @Override
    public void saveBook(Book book) {
        mCachedBooks.put(book.url, book);
        mLocalDataSource.saveBook(book);
    }

    private void getBooksFromLocalDataSource(@NonNull final LoadBooksCallback callback) {
        mLocalDataSource.getBooks(new LoadBooksCallback() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                refreshCache(books);
                callback.onBooksLoaded(new ArrayList<>(mCachedBooks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Book> books) {
        if (mCachedBooks == null) {
            mCachedBooks = new LinkedHashMap<>();
        }
        mCachedBooks.clear();
        for (Book book : books) {
            mCachedBooks.put(book.url, book);
        }
        mCacheIsDirty = false;
    }
}
