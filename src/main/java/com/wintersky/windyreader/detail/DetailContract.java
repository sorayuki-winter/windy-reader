package com.wintersky.windyreader.detail;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Chapter;

import java.util.List;

public interface DetailContract {

    interface View extends BaseView<Presenter> {

        void setChapters(List<Chapter> list);
    }

    interface Presenter extends BasePresenter<View> {

    }
}
