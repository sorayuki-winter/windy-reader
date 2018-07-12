package com.wintersky.windyreader.catalog;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Chapter;

public interface CatalogContract {

    interface View extends BaseView<Presenter> {

        void addChapter(Chapter chapter);

        void cListLoaded();
    }

    interface Presenter extends BasePresenter<View> {

    }
}
