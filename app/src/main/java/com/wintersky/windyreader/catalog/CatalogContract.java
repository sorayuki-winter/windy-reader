package com.wintersky.windyreader.catalog;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Book;

public interface CatalogContract {

    interface View extends BaseView {

        void setBook(Book book);
    }

    interface Presenter extends BasePresenter<View> {

    }
}
