package com.wintersky.windyreader.shelf;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.inject.Inject;

import io.realm.RealmResults;

import static com.wintersky.windyreader.util.Constants.WS;

public class ShelfPresenter implements ShelfContract.Presenter {

    private final Repository mRepository;
    private boolean isFirst = true;
    private ShelfContract.View mView;

    @Inject
    ShelfPresenter(Repository mRepository) {
        this.mRepository = mRepository;
    }

    @Override
    public void takeView(ShelfContract.View view) {
        mView = view;
        start();
    }

    @Override
    public void dropView() {
        mView = null;
    }

    private void start() {
        if (isFirst) {
            isFirst = false;
        } else {
            return;
        }

        mRepository.getShelf(new DataSource.GetShelfCallback() {
            @Override
            public void onLoaded(RealmResults<Book> list) {
                if (mView == null) return;
                mView.setShelf(list);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(bs));
                WS("Shelf - can not load shelf", bs.toString());
            }
        });
    }

    @Override
    public void getBook(String url) {
        mRepository.getBook(url, new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                if (mView == null) return;
                if (book.getTitle().isEmpty()) {
                    mView.getBookFinish();
                    return;
                }
                mRepository.saveBook(book);
                mView.getBookFinish(book);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(bs));
                WS("Shelf - get book fail", bs.toString());
                if (mView == null) return;
                mView.getBookFinish();
            }
        });
    }

    @Override
    public void deleteBook(String url) {
        mRepository.deleteBook(url);
    }
}
