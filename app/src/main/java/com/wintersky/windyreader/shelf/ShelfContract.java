package com.wintersky.windyreader.shelf;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Book;

import io.realm.RealmResults;

public interface ShelfContract {

    interface View extends BaseView<Presenter> {

        void setShelf(RealmResults<Book> list);

        void getBookFinish();

        void getBookFinish(Book book);
    }

    interface Presenter extends BasePresenter<View> {

        void getBook(String url);

        void deleteBook(String url);
    }
}
