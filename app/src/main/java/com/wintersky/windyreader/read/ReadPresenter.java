package com.wintersky.windyreader.read;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import java.util.Date;

import javax.inject.Inject;

import io.realm.Realm;

import static com.wintersky.windyreader.util.LogTools.LOG;

public class ReadPresenter implements ReadContract.Presenter {

    private final Repository mRepository;
    private final String mUrl;
    private ReadContract.View mView;
    private boolean isFirst = true;

    private Book mBook;

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
            public void onLoaded(final Book book) {
                mBook = book;

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                mBook.setLastRead(new Date());
                mBook.setHasNew(false);
                realm.commitTransaction();
                realm.close();

                Chapter chapter = mBook.getCurrent();
                if (chapter != null) {
                    loadChapter(chapter.getUrl());
                }

                mRepository.cacheBook(mUrl, new DataSource.CacheBookCallback() {
                    @Override
                    public void onCached() {
                        if (mView != null) {
                            mView.onBookCached();
                        }
                    }
                });
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                LOG("Read - get book fail", e);
            }
        });
    }

    @Override
    public void loadChapter(final String url) {
        mRepository.getChapter(url, new DataSource.GetChapterCallback() {
            @Override
            public void onLoaded(final Chapter chapter) {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                mBook.setIndex(chapter.getIndex());
                chapter.setRead(true);
                realm.commitTransaction();
                realm.close();

                if (mView != null) {
                    mView.setChapter(chapter);
                }
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                LOG("Read - get chapter fail", e);
                String msg = e.getMessage();
                if (msg.contains("timed out")) {
                    loadChapter(url);
                }
            }
        });
    }

    @Override
    public void prevChapter() {
        Chapter chapter = mBook.getPrev();
        if (chapter != null) {
            loadChapter(chapter.getUrl());
        }
    }

    @Override
    public void nextChapter() {
        Chapter chapter = mBook.getNext();
        if (chapter != null) {
            loadChapter(chapter.getUrl());
        }
    }
}
