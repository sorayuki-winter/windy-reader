package com.wintersky.windyreader.search;

import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View mView;

    private Repository mRepository;

    @Inject
    SearchPresenter(Repository mRepository) {
        this.mRepository = mRepository;
    }

    @Override
    public void takeView(SearchContract.View view) {
        mView = view;
    }

    @Override
    public void dropView() {
        mView = null;
    }
}
