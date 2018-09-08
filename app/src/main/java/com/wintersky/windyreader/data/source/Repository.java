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
    public void getBook(@NonNull final String url, @NonNull final GetBookCallback callback) {
        mLocalDataSource.getBook(url, new GetBookCallback() {
            @Override
            public void onLoaded(@NonNull Book book) {
                callback.onLoaded(book);
            }

            @Override
            public void onFailed(@NonNull Exception e) {
                mRemoteDataSource.getBook(url, callback);
            }
        });
    }

    @Override
    public void getCatalog(@NonNull final String url, @NonNull final GetCatalogCallback callback) {
        mLocalDataSource.getCatalog(url, new GetCatalogCallback() {
            @Override
            public void onLoaded(@NonNull final RealmList<Chapter> list) {
                callback.onLoaded(list);
            }

            @Override
            public void onFailed(@NonNull final Exception e) {
                mRemoteDataSource.getCatalog(url, callback);
            }
        });
    }

    @Override
    public void getContent(@NonNull final String url, @NonNull final GetContentCallback callback) {
        mLocalDataSource.getContent(url, new GetContentCallback() {
            @Override
            public void onLoaded(@NonNull String content) {
                callback.onLoaded(content);
            }

            @Override
            public void onFailed(@NonNull Exception e) {
                mRemoteDataSource.getContent(url, callback);
            }
        });
    }

    @Override
    public void saveBook(@NonNull final Book book, @NonNull final SaveBookCallback callback) {
        getBook(book.url, new GetBookCallback() {
            @Override
            public void onLoaded(@NonNull final Book book) {
                if (book.isManaged()) {
                    book.getRealm().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(@NonNull Realm realm) {
                            book.lastRead = new Date();
                        }
                    });
                    callback.onSaved(book);
                } else {
                    mRemoteDataSource.getCatalog(book.catalogUrl, new GetCatalogCallback() {
                        @Override
                        public void onLoaded(@NonNull RealmList<Chapter> list) {
                            book.catalog = list;
                            book.setLastRead(new Date());
                            book.setHasNew(true);
                            mLocalDataSource.saveBook(book, callback);
                        }

                        @Override
                        public void onFailed(@NonNull Exception e) {
                            callback.onFailed(e);
                        }
                    });
                }
            }

            @Override
            public void onFailed(@NonNull final Exception e) {
                callback.onFailed(e);
            }
        });
    }

    @Override
    public void deleteBook(@NonNull String url) {
        mLocalDataSource.deleteBook(url);
    }
}
