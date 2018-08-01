package com.wintersky.windyreader.detail;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

import static com.wintersky.windyreader.util.Constants.WS;

public class DetailPresenter implements DetailContract.Presenter {

    private final Repository mRepository;
    private final String mUrl;
    private DetailContract.View mView;

    @Inject
    DetailPresenter(Repository repository, String url) {
        mRepository = repository;
        mUrl = url;
    }

    @Override
    public void takeView(DetailContract.View view) {
        mView = view;
        start();
    }

    @Override
    public void dropView() {
        mView = null;
    }

    private void start() {
        mRepository.getBook(mUrl, new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                mView.setBook(book);
            }

            @Override
            public void onDataNotAvailable() {
                WS("get book fail");
            }
        });
    }

    @Override
    public void saveBook(String url) {
        mRepository.getBook(url, new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                mRepository.saveBook(book);
            }

            @Override
            public void onDataNotAvailable() {
                WS("get book fail(save)");
            }
        });
    }
}
