package com.wintersky.windyreader.read;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import javax.inject.Inject;

import static com.wintersky.windyreader.util.Constants.WS;

public class ReadPresenter implements ReadContract.Presenter {

    private boolean isFirst = true;

    private ReadContract.View mView;
    private Repository mRepository;
    private String[] urls;
    private Book mBook;
    private Chapter mChapter;

    @Inject
    ReadPresenter(Repository repository, String[] urls) {
        this.mRepository = repository;
        this.urls = urls;
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

        loadChapter(urls[0]);

        mRepository.getBook(urls[1], new DataSource.GetBookCallback() {
            @Override
            public void onBookLoaded(Book book) {
                mBook = book;
            }

            @Override
            public void onDataNotAvailable() {
                WS("Read", "get book fail");
            }
        });
    }

    @Override
    public void lastChapter() {
        mBook.setCurrentCUrl(mChapter.last);
        mRepository.saveBook(mBook);
        loadChapter(mChapter.last);
    }

    @Override
    public void nextChapter() {
        mBook.setCurrentCUrl(mChapter.next);
        mRepository.saveBook(mBook);
        loadChapter(mChapter.next);
    }

    private void loadChapter(String url) {
        mRepository.getChapter(url, new DataSource.GetChapterCallback() {
            @Override
            public void onChapterLoaded(Chapter chapter) {
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
