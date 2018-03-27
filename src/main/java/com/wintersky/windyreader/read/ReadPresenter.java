package com.wintersky.windyreader.read;

import android.util.Log;

import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

import static com.wintersky.windyreader.util.Constants.LT;

public class ReadPresenter implements ReadContract.Presenter {

    private ReadContract.View mView;

    private Repository mRepository;

    private String[] urls;

    @Inject
    ReadPresenter(Repository repository, String[] urls) {
        this.mRepository = repository;
        this.urls = urls;
    }

    @Override
    public void takeView(ReadContract.View view) {
        mView = view;
        start();
    }

    @Override
    public void dropView() {
        mView = null;
    }

    private void start() {
        mRepository.getChapter(urls[0], new DataSource.GetChapterCallback() {
            @Override
            public void onChapterLoaded(Chapter chapter) {
                mView.setChapter(chapter);
            }

            @Override
            public void onDataNotAvailable() {
                Log.i(LT, "get chapter fail");
            }
        });
    }
}
