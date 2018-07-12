package com.wintersky.windyreader.data.source;

import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmList;

import static com.wintersky.windyreader.util.Constants.WS;

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
    public void getCList(final String url, final LoadCListCallback callback) {
        getBook(url, new GetBookCallback() {
            @Override
            public void onLoaded(final Book book) {
                callback.onLoaded(book.getList());
                mRemoteDataSource.getCList(url, new LoadCListCallback() {
                    @Override
                    public void onLoaded(RealmList<Chapter> list) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        book.getList().clear();
                        book.getList().addAll(list);
                        realm.commitTransaction();
                    }

                    @Override
                    public void onDataNotAvailable() {
                        WS("Repository.getCList()", "get chapter list from remote fail");
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                WS("Repository.getCList()", "get book fail");
            }
        });
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
        mLocalDataSource.saveBook(book);
    }
}
