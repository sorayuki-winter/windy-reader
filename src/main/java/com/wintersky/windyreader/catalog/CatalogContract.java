package com.wintersky.windyreader.catalog;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Chapter;

import java.util.List;

public interface CatalogContract {

    interface View extends BaseView<Presenter> {
        void setChapterList(List<Chapter> list);
    }

    interface Presenter extends BasePresenter<View> {
    }
}
