package com.wintersky.windyreader.shelf;

import com.wintersky.windyreader.BasePresenter;
import com.wintersky.windyreader.BaseView;
import com.wintersky.windyreader.data.Book;

import io.realm.RealmResults;

public interface ShelfContract {

    interface View extends BaseView {

        void setShelf(RealmResults<Book> list);

        void onBookSaved(boolean ok);
    }

    interface Presenter extends BasePresenter<View> {

        void saveBook(String url);

        void deleteBook(String url);
    }
}
