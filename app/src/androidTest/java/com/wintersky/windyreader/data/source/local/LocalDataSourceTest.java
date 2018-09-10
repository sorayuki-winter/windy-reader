package com.wintersky.windyreader.data.source.local;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.SingleExecutors;

import org.junit.Before;
import org.junit.Test;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.wintersky.windyreader.util.CheckUtil.checkBook;
import static com.wintersky.windyreader.util.CheckUtil.checkShelf;
import static com.wintersky.windyreader.util.LogUtil.LOG;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LocalDataSourceTest {

    private Realm mRealm;
    private LocalDataSource mSource;

    private Book mBook;
    private Chapter mChapter;
    private String mContent;

    @Before
    public void setUp() {
        Looper.prepare();
        Context context = InstrumentationRegistry.getTargetContext();
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("test.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        mRealm = Realm.getInstance(config);
        mSource = new LocalDataSource(context, new SingleExecutors(), mRealm);

        mChapter = new Chapter();
        mChapter.setTitle("CHAPTER_TIT");
        mChapter.setUrl("http://book/chapter/0");
        mContent = "CHAPTER_CON";

        RealmList<Chapter> catalog = new RealmList<>();
        catalog.add(mChapter);

        mBook = new Book();
        mBook.setTitle("BOOK_TIT");
        mBook.setUrl("http://book");
        mBook.setCatalogUrl("http://book/chapter");
        mBook.setCatalog(catalog);
    }

    @Test
    public void tearDown() {
        Looper.loop();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull final Realm realm) {
                realm.deleteAll();
            }
        });
        mRealm.close();
    }

    private void quit() {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            looper.quitSafely();
        }
    }

    private void save() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull final Realm realm) {
                mRealm.copyToRealmOrUpdate(mBook);
            }
        });
    }

    @Test
    public void localGetShelf() {
        save();
        mSource.getShelf(new DataSource.GetShelfCallback() {
            @Override
            public void onLoaded(@NonNull RealmResults<Book> list) {
                checkShelf(list);
                quit();
            }
        });
    }

    @Test
    public void localGetBook() {
        save();
        mSource.getBook(mBook.getUrl(), new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(@NonNull Book book) {
                checkBook(book);
                quit();
            }

            @Override
            public void onFailed(@NonNull Throwable error) {
                fail(error.toString());
            }
        });
    }

    @Test
    public void localGetBookNoExist() {
        mSource.getBook("??", new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(@NonNull final Book book) {
                fail("book should not be found");
            }

            @Override
            public void onFailed(@NonNull final Throwable error) {
                LOG(error);
                assertTrue(error.toString(), error.toString().contains("Book not find"));
                quit();
            }
        });
    }

    @Test
    public void localSaveBook() {
        mSource.saveBook(mBook, new DataSource.SaveBookCallback() {
            @Override
            public void onSaved(@NonNull Book book) {
                quit();
            }

            @Override
            public void onFailed(@NonNull Throwable error) {
                fail(error.toString());
            }
        });
    }

    @Test
    public void localDeleteBook() {
        save();
        mSource.deleteBook(mBook.getUrl(), new DataSource.DeleteBookCallback() {
            @Override
            public void onDeleted() {
                quit();
            }

            @Override
            public void onFailed(@NonNull final Throwable error) {
                fail(error.toString());
            }
        });
    }

    @Test
    public void localSaveContentTo() {
        assertTrue(mSource.saveContentTo(mChapter.getCatalogUrl(), mChapter.getUrl(), mContent));
        quit();
    }

    @Test
    public void localGetContentFrom() {
        localSaveContentTo();
        String content = mSource.getContentFrom(mChapter.getCatalogUrl(), mChapter.getUrl());
        assertNotNull(content);
        quit();
    }
}
