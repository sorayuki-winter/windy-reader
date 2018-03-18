package com.wintersky.windyreader.read;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;

public interface ReadContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
