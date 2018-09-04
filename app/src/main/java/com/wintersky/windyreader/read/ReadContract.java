package com.wintersky.windyreader.read;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

public interface ReadContract {

    interface View extends BaseView {

        void setBook(Book book);

        void onBookCached();

        void setContent(Chapter chapter, String content, float progress);
    }

    interface Presenter extends BasePresenter<View> {

        void saveReadIndex(float index);

        void loadContent(Chapter chapter, float progress);
    }
}
