package com.wintersky.windyreader.data.source;

import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;

@Singleton
public class Repository implements DataSource {

    private final DataSource mLocalDataSource;

    private final DataSource mRemoteDataSource;

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
        mLocalDataSource.getBList(callback);
    }

    @Override
    public void getBook(String url, final GetBookCallback callback) {
        mLocalDataSource.getBook(url, new GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                callback.onLoaded(book);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getChapter(final String url, final GetChapterCallback callback) {
        mLocalDataSource.getChapter(url, new GetChapterCallback() {
            @Override
            public void onLoaded(final Chapter chapter) {
                mRemoteDataSource.getChapter(url, new GetChapterCallback() {
                    @Override
                    public void onLoaded(Chapter c) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        c.setNum(chapter.getNum());
                        realm.copyToRealmOrUpdate(c);
                        chapter.setContent(c.getContent());
                        realm.commitTransaction();
                        realm.close();
                        callback.onLoaded(chapter);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });

            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveBook(Book book) {
        mLocalDataSource.saveBook(book);
    }

    @Override
    public void updateCheck(String url, UpdateCheckCallback callback) {

    }
}
