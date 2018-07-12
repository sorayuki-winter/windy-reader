package com.wintersky.windyreader.catalog;

import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

import static com.wintersky.windyreader.util.Constants.WS;

public class CatalogPresenter implements CatalogContract.Presenter {

    private boolean isFirst = true;

    private CatalogContract.View mView;
    private Repository mRepository;
    private String mUrl;

    @Inject
    CatalogPresenter(Repository repository, String url) {
        mRepository = repository;
        mUrl = url;
    }

    @Override
    public void takeView(CatalogContract.View view) {
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

        mRepository.getCList(mUrl, new DataSource.LoadCListCallback() {
            @Override
            public void onLoading(Chapter chapter) {
                mView.addChapter(chapter);
            }

            @Override
            public void onLoaded() {
                mView.cListLoaded();
            }

            @Override
            public void onDataNotAvailable() {
                WS("Catalog Start", "get chapter list fail");
            }
        });
    }
}
