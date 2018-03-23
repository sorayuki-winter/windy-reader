package com.wintersky.windyreader.shelf;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;

public interface ShelfContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
