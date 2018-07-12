package com.wintersky.windyreader.data.source.local;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.SingleExecutors;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.wintersky.windyreader.util.Constants.WS;

public class LocalDataSourceTest {

    private LocalDataSource mSource;

    @Before
    public void setUp() {
        mSource = new LocalDataSource(new SingleExecutors());
    }

    @Test
    public void saveBook() {
        Book book = new Book();
        book.setUrl("http://zxzw.com/164588/");
        book.setTitle("合体双修");
        book.setCurrentCUrl("http://zxzw.com/164588/14192209/");
        mSource.saveBook(book);
    }

    @Test
    public void getLList() {
    }

    @Test
    public void getBList() {
        mSource.getBList(new DataSource.LoadBListCallback() {
            @Override
            public void onLoaded(List<Book> list) {
                if (list == null || list.isEmpty()) {
                    WS("book list empty");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (Book book : list) {
                    sb.append(book.getTitle()).append(" ").append(book.getUrl()).append("\n");
                }
                WS(sb.toString());
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Test
    public void getBook() {
        mSource.getBook("http://zxzw.com/164588/", new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                WS(book.getTitle() + " " + book.getUrl());
            }

            @Override
            public void onDataNotAvailable() {
                WS("not find");
            }
        });
    }

    @Test
    public void getCList() {
    }

    @Test
    public void getChapter() {
    }
}