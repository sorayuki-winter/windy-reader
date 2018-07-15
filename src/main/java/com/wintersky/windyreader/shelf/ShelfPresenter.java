package com.wintersky.windyreader.shelf;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

import io.realm.RealmResults;

import static com.wintersky.windyreader.util.Constants.WS;

public class ShelfPresenter implements ShelfContract.Presenter {

    private boolean isFirst = true;

    private ShelfContract.View mView;
    private final Repository mRepository;

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
                if (mView == null) return;
                mView.setBooks(list);
            }

            @Override
            public void onDataNotAvailable() {
                WS("can not load shelf");
            }
        });
    }
}
