package com.wintersky.windyreader.shelf;

import android.util.Log;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import java.util.List;

import javax.inject.Inject;

import static com.wintersky.windyreader.util.Constants.LT;

public class ShelfPresenter implements ShelfContract.Presenter {

    private ShelfContract.View mView;

    private Repository mRepository;

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
        mRepository.getBooks(new DataSource.LoadBooksCallback() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                mView.setBooks(books);
            }

            @Override
            public void onDataNotAvailable() {
                Log.i(LT, "can not load shelf");
            }
        });
    }
}
