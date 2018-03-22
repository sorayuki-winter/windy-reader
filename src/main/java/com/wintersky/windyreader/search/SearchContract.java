package com.wintersky.windyreader.search;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;

public interface SearchContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
