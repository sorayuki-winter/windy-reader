package com.wintersky.windyreader.shelf;

import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

import io.realm.RealmResults;

import static com.wintersky.windyreader.util.LogUtil.LOG;
import static com.wintersky.windyreader.util.LogUtil.LOGD;

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
            public void onLoaded(@NonNull RealmResults<Book> list) {
                if (mView != null) {
                    mView.setShelf(list);
                }
            }
        });
    }

    @Override
    public void saveBook(final String url) {
        Book bkSave = new Book();
        bkSave.setUrl(url);
        mRepository.saveBook(bkSave, new DataSource.SaveBookCallback() {
            @Override
            public void onSaved(@NonNull Book book) {
                if (mView != null) {
                    mView.onBookSaved(book);
                }
            }

            @Override
            public void onFailed(@NonNull Throwable error) {
                if (error.toString().contains("java.io.FileNotFoundException")) {
                    if (url.matches("https?://m\\..*")) {
                        String www = url.replaceFirst("m", "www");
                        saveBook(www);
                        LOG("try to connect to " + www, error);
                        return;
                    }
                }
                if (mView != null) {
                    mView.onBookSaved(url, error);
                }
                LOG("Shelf - save book fail", error);
            }
        });
    }

    @Override
    public void deleteBook(final String url) {
        mRepository.deleteBook(url, new DataSource.DeleteBookCallback() {
            @Override
            public void onDeleted() {
                LOGD("book deleted: " + url);
            }

            @Override
            public void onFailed(@NonNull final Throwable error) {
                LOG("Delete book fail: " + url, error);
            }
        });
    }
}
