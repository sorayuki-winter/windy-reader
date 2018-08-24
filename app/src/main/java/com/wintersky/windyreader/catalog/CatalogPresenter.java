package com.wintersky.windyreader.catalog;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.inject.Inject;

import static com.wintersky.windyreader.util.LogTools.LOG;

public class CatalogPresenter implements CatalogContract.Presenter {

    private final Repository mRepository;
    private final String mUrl;
    private boolean isFirst = true;
    private CatalogContract.View mView;

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
            public void onLoaded(final Book book) {
                if (mView == null) return;
                mView.setBook(book);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(bs));
                LOG("Catalog - get book fail", bs.toString());
            }
        });
    }
}
