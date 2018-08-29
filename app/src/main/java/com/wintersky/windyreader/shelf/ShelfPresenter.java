package com.wintersky.windyreader.shelf;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.wintersky.windyreader.util.LogUtil.LOG;

public class ShelfPresenter implements ShelfContract.Presenter {

    private final Repository mRepository;
    private ShelfContract.View mView;
    private boolean isFirst = true;

    @Inject
    ShelfPresenter(Repository mRepository) {
        this.mRepository = mRepository;
    }

    @Override
    public void takeView(ShelfContract.View view) {
        mView = view;
        start();
    }

    @Override
    public void dropView() {
        mView = null;
    }

    private void start() {
        if (isFirst) {
            isFirst = false;
        } else {
            return;
        }
        mRepository.getShelf(new DataSource.GetShelfCallback() {
            @Override
            public void onLoaded(RealmResults<Book> list) {
                if (mView != null) {
                    mView.setShelf(list);
                }
            }
        });
    }

    @Override
    public void saveBook(String url) {
        Realm realm = Realm.getDefaultInstance();
        if (realm.where(Book.class).equalTo("url", url).findFirst() != null) {
            mView.onBookSaved(true);
            realm.close();
            return;
        }
        realm.close();
        mRepository.getBook(url, new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                mRepository.saveBook(book, new DataSource.SaveBookCallback() {
                    @Override
                    public void onSaved() {
                        if (mView != null) {
                            mView.onBookSaved(true);
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        LOG("Shelf - save book fail", e);
                        if (mView != null) {
                            mView.onBookSaved(true);
                        }
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                LOG("Shelf - save book fail", e);
                if (mView != null) {
                    mView.onBookSaved(false);
                }
            }
        });
    }

    @Override
    public void deleteBook(String url) {
        mRepository.deleteBook(url);
    }
}
