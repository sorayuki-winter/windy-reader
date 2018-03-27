package com.wintersky.windyreader.detail;

import android.util.Log;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import java.util.List;

import javax.inject.Inject;

import static com.wintersky.windyreader.util.Constants.LT;

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
            public void onBookLoaded(Book book) {
                mView.setBook(book);
            }

            @Override
            public void onDataNotAvailable() {
                Log.i(LT, "get book fail");
            }
        });

        mRepository.getChapters(bookUrl, new DataSource.LoadChaptersCallback() {
            @Override
            public void onChaptersLoaded(List<Chapter> list) {
                mView.setChapters(list);
            }

            @Override
            public void onDataNotAvailable() {
                Log.i(LT, "load chapter list fail");
            }
        });
    }

    @Override
    public void saveBook(String url) {
        mRepository.getBook(url, new DataSource.GetBookCallback() {
            @Override
            public void onBookLoaded(Book book) {
                mRepository.saveBook(book);
            }

            @Override
            public void onDataNotAvailable() {
                Log.i(LT, "get book fail(save)");
            }
        });
    }
}
