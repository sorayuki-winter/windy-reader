package com.wintersky.windyreader.read;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Chapter;

public interface ReadContract {

    interface View extends BaseView<Presenter> {

        void setChapter(Chapter chapter);
    }

    interface Presenter extends BasePresenter<View> {

        void lastChapter();

        void nextChapter();
    }
}
