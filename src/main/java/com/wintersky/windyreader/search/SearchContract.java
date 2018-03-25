package com.wintersky.windyreader.search;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Book;

import java.util.List;

public interface SearchContract {

    interface View extends BaseView<Presenter> {

        void setResult(List<Book> list);
    }

    interface Presenter extends BasePresenter<View> {

        void search(String url, String keyword);
    }
}
