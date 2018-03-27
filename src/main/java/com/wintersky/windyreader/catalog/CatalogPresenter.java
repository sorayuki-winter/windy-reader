package com.wintersky.windyreader.catalog;

import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

public class CatalogPresenter implements CatalogContract.Presenter {

    private CatalogContract.View mView;

    private Repository mRepository;

    @Inject
    CatalogPresenter(Repository repository) {
        this.mRepository = repository;
    }

    @Override
    public void takeView(CatalogContract.View view) {
        mView = view;
    }

    @Override
    public void dropView() {
        mView = null;
    }
}
