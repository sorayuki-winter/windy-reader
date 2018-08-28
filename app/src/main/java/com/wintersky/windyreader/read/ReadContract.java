package com.wintersky.windyreader.read;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Chapter;

public interface ReadContract {

    interface View extends BaseView {

        void setChapter(Chapter chapter);

        void onBookCached();
    }

    interface Presenter extends BasePresenter<View> {

        void loadChapter(String url);

        void prevChapter();

        void nextChapter();
    }
}
