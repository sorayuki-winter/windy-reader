package com.wintersky.windyreader.data.source;

import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmList;

@Singleton
public class Repository implements DataSource {

    private final DataSource mLocalDataSource;
    private final DataSource mRemoteDataSource;

    @Inject
    Repository(@Remote DataSource remoteDataSource, @Local DataSource localDataSource) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
    }

    @Override
    public void getShelf(@NonNull final GetShelfCallback callback) {
        mLocalDataSource.getShelf(callback);
    }

    @Override
    public void getBook(@NonNull final String bkUrl, @NonNull final GetBookCallback callback) {
        mLocalDataSource.getBook(bkUrl, new GetBookCallback() {
            @Override
            public void onLoaded(@NonNull Book book) {
                callback.onLoaded(book);
            }

            @Override
            public void onFailed(@NonNull Throwable error) {
                mRemoteDataSource.getBook(bkUrl, callback);
            }
        });
    }

    @Override
    public void getCatalog(@NonNull final Book book, @NonNull final GetCatalogCallback callback) {
        mLocalDataSource.getCatalog(book, new GetCatalogCallback() {
            @Override
            public void onLoaded(@NonNull final RealmList<Chapter> list) {
                callback.onLoaded(list);
            }

            @Override
            public void onFailed(@NonNull final Throwable error) {
                mRemoteDataSource.getCatalog(book, callback);
            }
        });
    }

    @Override
    public void getContent(@NonNull final Chapter chapter, @NonNull final GetContentCallback callback) {
        mLocalDataSource.getContent(chapter, new GetContentCallback() {
            @Override
            public void onLoaded(@NonNull String content) {
                callback.onLoaded(content);
            }

            @Override
            public void onFailed(@NonNull Throwable error) {
                mRemoteDataSource.getContent(chapter, callback);
            }
        });
    }

    @Override
    public void saveBook(@NonNull final Book book, @NonNull final SaveBookCallback callback) {
        getBook(book.getUrl(), new GetBookCallback() {
            @Override
            public void onLoaded(@NonNull final Book book) {
                if (book.isManaged()) {
                    book.getRealm().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(@NonNull Realm realm) {
                            book.setLastRead(new Date());
                        }
                    });
                    callback.onSaved(book);
                } else {
                    mRemoteDataSource.getCatalog(book, new GetCatalogCallback() {
                        @Override
                        public void onLoaded(@NonNull RealmList<Chapter> list) {
                            book.setCatalog(list);
                            book.setLastRead(new Date());
                            book.setHasNew(true);
                            mLocalDataSource.saveBook(book, callback);
                        }

                        @Override
                        public void onFailed(@NonNull Throwable error) {
                            callback.onFailed(error);
                        }
                    });
                }
            }

            @Override
            public void onFailed(@NonNull final Throwable error) {
                callback.onFailed(error);
            }
        });
    }

    @Override
    public void deleteBook(@NonNull String bkUrl, @NonNull DeleteBookCallback callback) {
        mLocalDataSource.deleteBook(bkUrl, callback);
    }
}
