package com.wintersky.windyreader.catalog;

import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

import static com.wintersky.windyreader.util.LogUtil.LOG;

public class CatalogPresenter implements CatalogContract.Presenter {

    private final Repository mRepository;
    private final String mUrl;
    private CatalogContract.View mView;
    private boolean isFirst = true;

    @Inject
    CatalogPresenter(Repository repository, String url) {
        mRepository = repository;
        mUrl = url;
    }

    @Override
    public void takeView(CatalogContract.View view) {
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
        mRepository.getBook(mUrl, new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(@NonNull final Book book) {
                if (mView != null) {
                    mView.setBook(book);
                }
            }

            @Override
            public void onFailed(@NonNull Throwable error) {
                LOG("Catalog - get book fail", error);
            }
        });
    }
}
