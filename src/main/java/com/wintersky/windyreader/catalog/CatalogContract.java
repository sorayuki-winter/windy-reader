package com.wintersky.windyreader.catalog;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Chapter;

import io.realm.RealmList;

public interface CatalogContract {

    interface View extends BaseView<Presenter> {

        void setCList(RealmList<Chapter> list);
    }

    interface Presenter extends BasePresenter<View> {

    }
}
