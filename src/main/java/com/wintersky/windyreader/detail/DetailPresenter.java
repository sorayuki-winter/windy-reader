package com.wintersky.windyreader.detail;

import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

public class DetailPresenter implements DetailContract.Presenter {

    private DetailContract.View mView;

    private Repository mRepository;

    @Inject
    DetailPresenter(Repository mRepository) {
        this.mRepository = mRepository;
    }

    @Override
    public void takeView(DetailContract.View view) {
        mView = view;
    }

    @Override
    public void dropView() {
        mView = null;
    }
}
