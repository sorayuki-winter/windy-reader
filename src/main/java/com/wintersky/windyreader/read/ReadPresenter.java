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
    private Realm mRealm;
    private String[] mUrls;
    private Book mBook;
    private Chapter mChapter;

    @Inject
    ReadPresenter(Repository repository, Realm realm, String[] urls) {
        mRepository = repository;
        mRealm = realm;
        mUrls = urls;
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

        getChapter(mUrls[0]);

        mRepository.getBook(mUrls[1], new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                mBook = book;
            }

            @Override
            public void onDataNotAvailable() {
                WS("Read", "get book fail");
            }
        });
    }

    @Override
    public void loadChapter(String url) {
        mRealm.beginTransaction();
        mBook.setCurrentUrl(url);
        mRealm.commitTransaction();
        getChapter(url);
    }

    @Override
    public void prevChapter() {
        mRealm.beginTransaction();
        mBook.setCurrentUrl(mChapter.getPrev());
        mRealm.commitTransaction();
        getChapter(mChapter.getPrev());
    }

    @Override
    public void nextChapter() {
        mRealm.beginTransaction();
        mBook.setCurrentUrl(mChapter.getNext());
        mRealm.commitTransaction();
        getChapter(mChapter.getNext());
    }

    private void getChapter(String url) {
        mRepository.getChapter(url, new DataSource.GetChapterCallback() {
            @Override
            public void onLoaded(Chapter chapter) {
                mChapter = chapter;
                mView.setChapter(chapter);
            }

            @Override
            public void onDataNotAvailable() {
                WS("Read", "get chapter fail");
            }
        });
    }
}
