package com.wintersky.windyreader.shelf;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Book;

import java.util.List;

public interface ShelfContract {

    interface View extends BaseView<Presenter> {
        void setBooks(List<Book> list);
    }

    interface Presenter extends BasePresenter<View> {

    }
}
