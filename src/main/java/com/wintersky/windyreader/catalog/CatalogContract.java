package com.wintersky.windyreader.catalog;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;

public interface CatalogContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
