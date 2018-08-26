package com.wintersky.windyreader.data.source;

import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmList;

@Singleton
public class Repository implements DataSource {

    private final DataSource mLocalDataSource;
    private final DataSource mRemoteDataSource;
    private final Realm mRealm;

    @Inject
    Repository(@Remote DataSource remoteDataSource, @Local DataSource localDataSource, Realm realm) {
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
        mRealm = realm;
    }

    @Override
    public void getShelf(@NonNull final GetShelfCallback callback) {
        mLocalDataSource.getShelf(callback);
    }

    @Override
    public void getBook(final String url, final GetBookCallback callback) {
        mLocalDataSource.getBook(url, new GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                callback.onLoaded(book);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                mRemoteDataSource.getBook(url, callback);
            }
        });
    }

    @Override
    public void getCatalog(String url, GetCatalogCallback callback) {
        mRemoteDataSource.getCatalog(url, callback);
    }

    @Override
    public void getChapter(final String url, final GetChapterCallback callback) {
        mLocalDataSource.getChapter(url, new GetChapterCallback() {
            @Override
            public void onLoaded(final Chapter chapterL) {
                if (chapterL.getContent() != null) {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(@NonNull Realm realm) {
                            chapterL.setRead(true);
                        }
                    });
                    callback.onLoaded(chapterL);
                    return;
                }

                mRemoteDataSource.getChapter(url, new GetChapterCallback() {
                    @Override
                    public void onLoaded(final Chapter chapterR) {
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                chapterL.setContent(chapterR.getContent());
                                chapterL.setRead(true);
                            }
                        });
                        callback.onLoaded(chapterL);
                    }

                    @Override
                    public void onDataNotAvailable(Exception e) {
                        callback.onDataNotAvailable(e);
                    }
                });
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                callback.onDataNotAvailable(e);
            }
        });
    }

    @Override
    public void saveBook(Book book) {
        mLocalDataSource.saveBook(book);
    }

    @Override
    public void deleteBook(String url) {
        mLocalDataSource.deleteBook(url);
    }

    @Override
    public void updateCheck(final String url, final UpdateCheckCallback callback) {
        mLocalDataSource.getBook(url, new GetBookCallback() {
            @Override
            public void onLoaded(final Book book) {
                mRemoteDataSource.getCatalog(book.getCatalogUrl(), new GetCatalogCallback() {
                    @Override
                    public void onLoaded(List<Chapter> list) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        RealmList<Chapter> catalog = book.getCatalog();
                        catalog.addAll(list.subList(catalog.size(), list.size()));
                        realm.commitTransaction();
                        realm.close();
                        callback.onChecked();
                    }

                    @Override
                    public void onDataNotAvailable(Exception e) {
                        callback.onDataNotAvailable(e);
                    }
                });
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                callback.onDataNotAvailable(e);
            }
        });
    }

    @Override
    public void cacheChapter(Chapter chapter) {
        mLocalDataSource.cacheChapter(chapter);
    }
}
