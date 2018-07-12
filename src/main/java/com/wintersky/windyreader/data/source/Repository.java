package com.wintersky.windyreader.data.source;

import android.support.annotation.NonNull;

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
    public void getLList(LoadLListCallback callback) {
        mLocalDataSource.getLList(callback);
    }

    @Override
    public void searchBook(String url, String key, SearchBookCallback callback) {
        mRemoteDataSource.searchBook(url, key, callback);
    }

    @Override
    public void getBList(@NonNull final LoadBListCallback callback) {
        if (mCachedBooks != null && !mCacheIsDirty) {
            callback.onLoaded(new ArrayList<>(mCachedBooks.values()));
            return;
        }
        getBooksFromLocalDataSource(callback);
    }

    @Override
    public void getBook(String url, final GetBookCallback callback) {
        if (mCachedBooks == null) {
            mCachedBooks = new LinkedHashMap<>();
        }
        Book cachedBook = mCachedBooks.get(url);

        // Respond immediately with cache if available
        if (cachedBook != null) {
            callback.onLoaded(cachedBook);
            return;
        }

        mLocalDataSource.getBook(url, new GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedBooks == null) {
                    mCachedBooks = new LinkedHashMap<>();
                }
                mCachedBooks.put(book.getUrl(), book);
                callback.onLoaded(book);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getCList(final String url, final LoadCListCallback callback) {
        mRemoteDataSource.getCList(url, callback);
    }

    @Override
    public void getChapter(final String url, final GetChapterCallback callback) {
        mLocalDataSource.getChapter(url, new GetChapterCallback() {
            @Override
            public void onLoaded(Chapter chapter) {
                if (chapter.getContent() != null)
                    callback.onLoaded(chapter);
                else
                    mRemoteDataSource.getChapter(url, callback);
            }

            @Override
            public void onDataNotAvailable() {
                mRemoteDataSource.getChapter(url, callback);
            }
        });
    }

    @Override
    public void saveBook(Book book) {
        mCachedBooks.put(book.getUrl(), book);
        mLocalDataSource.saveBook(book);
    }

    private void getBooksFromLocalDataSource(@NonNull final LoadBListCallback callback) {
        mLocalDataSource.getBList(new LoadBListCallback() {
            @Override
            public void onLoaded(List<Book> list) {
                refreshCache(list);
                callback.onLoaded(new ArrayList<>(mCachedBooks.values()));
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
            mCachedBooks.put(book.getUrl(), book);
        }
        mCacheIsDirty = false;
    }
}
