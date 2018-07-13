package com.wintersky.windyreader.read;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

import io.realm.Realm;

import static com.wintersky.windyreader.util.Constants.WS;

public class ReadPresenter implements ReadContract.Presenter {

    private boolean isFirst = true;

    private ReadContract.View mView;
    private Repository mRepository;
    private String mUrl;
    private Book mBook;
    private Chapter mChapter;

    @Inject
    ReadPresenter(Repository repository, String url) {
        mRepository = repository;
        mUrl = url;
    }

    @Override
    public void takeView(ReadContract.View view) {
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

        mRepository.getBook(mUrl, new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                mBook = book;
                loadChapter(book.getChapter().getUrl());
            }

            @Override
            public void onDataNotAvailable() {
                WS("Read", "get book fail");
            }
        });
    }

    @Override
    public void loadChapter(final String url) {
        mRepository.getChapter(url, new DataSource.GetChapterCallback() {
            @Override
            public void onLoaded(Chapter chapter) {
                mChapter = chapter;
                mView.setChapter(chapter);
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                mBook.setChapter(chapter);
                realm.commitTransaction();
                realm.close();
            }

            @Override
            public void onDataNotAvailable() {
                WS("Read", "get chapter fail");
            }
        });
    }

    @Override
    public void prevChapter() {
        loadChapter(mChapter.getPrev());
    }

    @Override
    public void nextChapter() {
        loadChapter(mChapter.getNext());
    }
}
