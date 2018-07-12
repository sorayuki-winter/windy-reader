package com.wintersky.windyreader.detail;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

import static com.wintersky.windyreader.util.Constants.WS;

public class DetailPresenter implements DetailContract.Presenter {

    private DetailContract.View mView;

    private Repository mRepository;

    private String bookUrl;

    @Inject
    DetailPresenter(Repository repository, String bookUrl) {
        this.mRepository = repository;
        this.bookUrl = bookUrl;
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
        mRepository.getBook(bookUrl, new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                mView.setBook(book);
            }

            @Override
            public void onDataNotAvailable() {
                WS("get book fail");
            }
        });

        mRepository.getCList(bookUrl, new DataSource.LoadCListCallback() {
            @Override
            public void onLoading(Chapter chapter) {

            }

            @Override
            public void onLoaded() {
                mView.setChapters(null);
            }

            @Override
            public void onDataNotAvailable() {
                WS("load chapter list fail");
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
