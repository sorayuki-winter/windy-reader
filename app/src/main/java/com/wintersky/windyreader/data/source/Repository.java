package com.wintersky.windyreader.data.source;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.local.LocalDataSource;
import com.wintersky.windyreader.data.source.remote.RemoteDataSource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmList;

import static com.wintersky.windyreader.util.LogTools.LOG;

@Singleton
public class Repository implements DataSource, DataSource.Repository {

    private final LocalDataSource mLocalDataSource;
    private final RemoteDataSource mRemoteDataSource;
    private final Realm mRealm;
    private CacheBookTask mCacheBookTask = null;

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

    @Inject
    Repository(LocalDataSource localDataSource, RemoteDataSource remoteDataSource, Realm realm) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
        mRealm = realm;
    }

    @Override
    public void cacheBook(String url, CacheBookCallback callback) {
        if (mCacheBookTask != null) {
            mCacheBookTask.cancel(true);
        }
        mCacheBookTask = new CacheBookTask();
        mCacheBookTask.execute(mLocalDataSource, mRemoteDataSource, callback, url);
    }

    private static class CacheBookTask extends AsyncTask<Object, Void, Void> {

        private CacheBookCallback mCallback;

        @Override
        protected Void doInBackground(Object... objects) {
            final LocalDataSource local = (LocalDataSource) objects[0];
            final RemoteDataSource remote = (RemoteDataSource) objects[1];
            mCallback = (CacheBookCallback) objects[2];
            String bookUrl = (String) objects[3];

            Realm realm = Realm.getDefaultInstance();
            Book book = realm.where(Book.class).equalTo("url", bookUrl).findFirst();
            if (book != null) {
                List<Chapter> catalog = book.getCatalog();
                for (int i = 0; i < catalog.size(); i++) {
                    if (isCancelled())
                        break;
                    final Chapter c = catalog.get(i);
                    if (c == null) continue;
                    if (!local.isContentExist(c.getUrl())) {
                        try {
                            String content = remote.getChapterFrom(c.getUrl()).getContent();
                            local.saveContentTo(c.getUrl(), content);
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
