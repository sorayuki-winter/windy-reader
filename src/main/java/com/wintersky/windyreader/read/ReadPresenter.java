package com.wintersky.windyreader.read;

import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

public class ReadPresenter implements ReadContract.Presenter {

    private ReadContract.View mView;

    private Repository mRepository;

    private String chapterUrl;

    @Inject
    public ReadPresenter(Repository mRepository, String chapterUrl) {
        this.mRepository = mRepository;
        this.chapterUrl = chapterUrl;
    }

    @Override
    public void takeView(ReadContract.View view) {
        mView = view;
        loadChapter();
    }

    @Override
    public void dropView() {
        mView = null;
    }

    private void loadChapter() {

    }
}
