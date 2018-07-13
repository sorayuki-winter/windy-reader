package com.wintersky.windyreader.data.source.local;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.SingleExecutors;

import org.junit.Before;
import org.junit.Test;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.wintersky.windyreader.util.Constants.WS;

public class LocalDataSourceTest {

    private LocalDataSource mSource;

    @Before
    public void setUp() {
        mSource = new LocalDataSource(new SingleExecutors());
    }

    @Test
    public void clearCList() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(Chapter.class);
        realm.commitTransaction();
    }

    @Test
    public void saveBook() {
        Book book = new Book();
        book.setUrl("http://zxzw.com/164588/");
        book.setTitle("合体双修");
        Chapter chapter = new Chapter();
        chapter.setNum(83);
        chapter.setUrl("http://zxzw.com/164588/14192209/");
        book.setChapter(chapter);
        mSource.saveBook(book);
    }

    @Test
    public void getLList() {
    }

    @Test
    public void getBList() {
        mSource.getBList(new DataSource.LoadBListCallback() {
            @Override
            public void onLoaded(RealmResults<Book> list) {
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