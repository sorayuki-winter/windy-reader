package com.wintersky.windyreader.shelf;

import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

public class ShelfPresenter implements ShelfContract.Presenter {

    private ShelfContract.View mView;

    private Repository mRepository;

    @Inject
    public ShelfPresenter(Repository mRepository) {
        this.mRepository = mRepository;
    }

    @Override
    public void takeView(ShelfContract.View view) {
        mView = view;
    }

    @Override
    public void dropView() {
        mView = null;
    }
}
