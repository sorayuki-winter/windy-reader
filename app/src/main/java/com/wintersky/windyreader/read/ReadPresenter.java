package com.wintersky.windyreader.read;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.inject.Inject;

import io.realm.Realm;

import static com.wintersky.windyreader.util.Constants.WS;

public class ReadPresenter implements ReadContract.Presenter {

    private final Repository mRepository;
    private final String mUrl;
    private boolean isFirst = true;
    private ReadContract.View mView;
    private Book mBook;
    private Chapter mChapter;
    private boolean loading = false;

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
                if (book.getCurrent() != null) {
                    loadChapter(book.getCurrent().getUrl());
                }
                mRepository.updateCheck(book.getUrl(), new DataSource.UpdateCheckCallback() {
                    @Override
                    public void onChecked() {
                        if (mChapter != null || loading) return;
                        if (mBook.getCurrent() == null) {
                            Chapter chapter = mBook.getCatalog().first();
                            if (chapter != null) {
                                loadChapter(chapter.getUrl());
                            }
                        } else {
                            loadChapter(mBook.getCurrent().getUrl());
                        }
                    }

                    @Override
                    public void onDataNotAvailable(Exception e) {
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        e.printStackTrace(new PrintStream(bs));
                        WS("Read - update check fail", bs.toString());
                    }
                });
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(bs));
                WS("Read - get book fail", bs.toString());
            }
        });
    }

    @Override
    public void loadChapter(final String url) {
        loading = true;
        mRepository.getChapter(url, new DataSource.GetChapterCallback() {
            @Override
            public void onLoaded(Chapter chapter) {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                mBook.setIndex(chapter.getIndex());
                realm.commitTransaction();
                realm.close();

                loading = false;
                mChapter = chapter;
                if (mView == null) return;
                mView.setChapter(chapter);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(bs));
                WS("Read - get chapter fail", bs.toString());
                String msg = e.getMessage();
                if (msg.contains("connect timed out")) {
                    loadChapter(url);
                }
            }
        });
    }

    @Override
    public void prevChapter() {
        if (mBook.getPrev() != null) {
            loadChapter(mBook.getPrev().getUrl());
        }
    }

    @Override
    public void nextChapter() {
        if (mBook.getNext() != null) {
            loadChapter(mBook.getNext().getUrl());
        }
    }
}
