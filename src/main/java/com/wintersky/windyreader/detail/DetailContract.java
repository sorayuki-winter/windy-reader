package com.wintersky.windyreader.detail;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;

public interface DetailContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
