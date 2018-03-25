package com.wintersky.windyreader.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wintersky.windyreader.data.Book;

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

    Map<String, Book> mCachedBooks;

    boolean mCacheIsDirty = false;

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
        Book cachedBook = getBookWithId(bookUrl);

        // Respond immediately with cache if available
        if (cachedBook != null) {
            callback.onBookLoaded(cachedBook);
            return;
        }

        mLocalDataSource.getBook(bookUrl, new GetBookCallback() {
            @Override
            public void onBookLoaded(Book book) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedBooks == null) {
                    mCachedBooks = new LinkedHashMap<>();
                }
                mCachedBooks.put(book.title, book);
                callback.onBookLoaded(book);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getChapters(String bookUrl, LoadChaptersCallback callback) {
        mRemoteDataSource.getChapters(bookUrl, callback);
    }

    @Override
    public void getChapter(String chapterUrl, GetChapterCallback callback) {
        mRemoteDataSource.getChapter(chapterUrl, callback);
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
            mCachedBooks.put(book.title, book);
        }
        mCacheIsDirty = false;
    }

    @Nullable
    private Book getBookWithId(@NonNull String id) {
        //checkNotNull(id);
        if (mCachedBooks == null || mCachedBooks.isEmpty()) {
            return null;
        } else {
            return mCachedBooks.get(id);
        }
    }
}
