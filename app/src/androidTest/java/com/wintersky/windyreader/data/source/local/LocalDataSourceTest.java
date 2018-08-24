package com.wintersky.windyreader.data.source.local;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.SingleExecutors;

import org.junit.Before;
import org.junit.Test;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.wintersky.windyreader.util.LogTools.LOG;

public class LocalDataSourceTest {

    private LocalDataSource mSource;

    @Before
    public void setUp() {
        mSource = new LocalDataSource(new SingleExecutors(), Realm.getDefaultInstance());
    }

    @Test
    public void clearShelf() {
        clearCatalog();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(Book.class);
        realm.commitTransaction();
        realm.close();
    }

    @Test
    public void clearCatalog() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(Chapter.class);
        realm.commitTransaction();
        realm.close();
    }

    @Test
    public void saveBook() {
        Book book = new Book();
        book.setUrl("http://zxzw.com/164588/");
        book.setCatalogUrl("http://zxzw.com/164588/");
        book.setTitle("合体双修");
        mSource.saveBook(book);
    }

    @Test
    public void getShelf() {
        mSource.getShelf(new DataSource.GetShelfCallback() {
            @Override
            public void onLoaded(RealmResults<Book> list) {
                if (list == null || list.isEmpty()) {
                    LOG("book list empty");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (Book book : list) {
                    sb.append(book.getTitle()).append(" ").append(book.getUrl()).append("\n");
                }
                LOG(sb.toString());
            }

            @Override
            public void onDataNotAvailable(Exception e) {

            }
        });
    }

    @Test
    public void getBook() {
        mSource.getBook("http://zxzw.com/164588/", new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(Book book) {
                LOG(book.getTitle() + " " + book.getUrl());
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                LOG("not find");
            }
        });
    }

    @Test
    public void getCatalog() {

    }

    @Test
    public void getChapter() {

    }
}