package com.wintersky.windyreader.data.source;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.local.LocalDataSource;
import com.wintersky.windyreader.data.source.remote.RemoteDataSource;

import org.keplerproject.luajava.LuaException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.wintersky.windyreader.util.LogTools.LOG;

@Singleton
public class Repository implements DataSource, DataSource.Repository {

    private final LocalDataSource mLocalDataSource;
    private final RemoteDataSource mRemoteDataSource;
    private final Realm mRealm;

    private CacheBookTask mCacheBookTask = null;

    @Inject
    Repository(LocalDataSource localDataSource, RemoteDataSource remoteDataSource, Realm realm) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
        mRealm = realm;
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<Book> results = realm.where(Book.class).findAll();
                for (Book book : results.createSnapshot()) {
                    try {
                        final List<Chapter> list = mRemoteDataSource.getCatalogFrom(book.getCatalogUrl());
                        final List<Chapter> catalog = book.getCatalog();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                catalog.addAll(list.subList(catalog.size(), list.size()));
                            }
                        });
                    } catch (LuaException | IOException e) {
                        LOG(e);
                    }
                }
                realm.close();
            }
        }, 0, 5, TimeUnit.MINUTES);
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
    public void saveBook(final Book book) {
        mRemoteDataSource.getCatalog(book.getCatalogUrl(), new GetCatalogCallback() {
            @Override
            public void onLoaded(List<Chapter> list) {
                if (book.getCatalog() == null) {
                    book.setCatalog(new RealmList<Chapter>());
                }
                book.getCatalog().clear();
                book.getCatalog().addAll(list);
                mLocalDataSource.saveBook(book);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                LOG(e);
            }
        });
    }

    @Override
    public void deleteBook(String url) {
        mLocalDataSource.deleteBook(url);
    }

    @Override
    public void cacheChapter(Chapter chapter) {
        mLocalDataSource.cacheChapter(chapter);
    }

    @Override
    public void cacheBook(String url, CacheBookCallback callback) {
        if (mCacheBookTask != null) {
            mCacheBookTask.cancel(true);
        }
        mCacheBookTask = new CacheBookTask(mLocalDataSource, mRemoteDataSource, callback);
        mCacheBookTask.execute(url);
    }

    private static class CacheBookTask extends AsyncTask<String, Void, Void> {

        private LocalDataSource mLocal;
        private RemoteDataSource mRemote;
        private CacheBookCallback mCallback;

        CacheBookTask(LocalDataSource localDataSource, RemoteDataSource remoteDataSource, CacheBookCallback callback) {
            mLocal = localDataSource;
            mRemote = remoteDataSource;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String bookUrl = strings[0];

            Realm realm = Realm.getDefaultInstance();
            Book book = realm.where(Book.class).equalTo("url", bookUrl).findFirst();
            if (book != null) {
                List<Chapter> catalog = book.getCatalog();
                for (int i = 0; i < catalog.size(); i++) {
                    if (isCancelled()) {
                        break;
                    }
                    final Chapter c = catalog.get(i);
                    if (c == null) {
                        continue;
                    }
                    if (!mLocal.isContentExist(c.getUrl())) {
                        try {
                            String content = mRemote.getChapterFrom(c.getUrl()).getContent();
                            mLocal.saveContentTo(c.getUrl(), content);
                        } catch (Exception e) {
                            LOG(e);
                        }
                    }
                }
            } else {
                LOG(new Exception("book not find: " + bookUrl));
            }
            realm.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mCallback.onCached();
        }
    }
}
