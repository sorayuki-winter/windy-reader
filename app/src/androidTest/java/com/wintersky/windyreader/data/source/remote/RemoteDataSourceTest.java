package com.wintersky.windyreader.data.source.remote;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.di.Component;
import com.wintersky.windyreader.util.SingleExecutors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static com.wintersky.windyreader.util.LogTools.LOG;
import static org.junit.Assert.assertNotNull;

public class RemoteDataSourceTest {

    private RemoteDataSource mSource;
    private List<String> bookUrl;
    private List<String> catalogUrl;
    private List<String> chapterUrl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = InstrumentationRegistry.getTargetContext();
        mSource = new RemoteDataSource(context, new SingleExecutors(), Component.get().getOkHttpClient());

        bookUrl = new ArrayList<>();
        catalogUrl = new ArrayList<>();
        chapterUrl = new ArrayList<>();

        bookUrl.add("http://www.8wenku.com/book/1871");
        catalogUrl.add("http://www.8wenku.com/book/1871");
        chapterUrl.add("http://www.8wenku.com/chapter/view?id=1871&chapter_no=1");

        bookUrl.add("http://zxzw.com/164718/");
        catalogUrl.add("http://zxzw.com/164718/");
        chapterUrl.add("http://zxzw.com/164718/14181340/");
    }

    @Test
    public void getBook() throws Exception {
        for (String url : bookUrl) {
            Book book = mSource.getBookFromRemote(url);
            assertNotNull("book null: " + url, book);
            bookCheck(book);
        }
    }

    @Test
    public void getCatalog() throws Exception {
        for (String url : catalogUrl) {
            long st = System.currentTimeMillis();
            final List<Chapter> list = mSource.getCatalogFromRemote(url);
            long et = System.currentTimeMillis();
            assertNotNull("chapter list null: " + url, list);
            catalogCheck(list);
            LOG(String.format("cast: %d ms", et - st));
        }
    }

    @Test
    public void getChapter() throws Exception {
        for (String url : chapterUrl) {
            Chapter chapter = mSource.getChapterFromRemote(url);
            assertNotNull("chapter null: " + url, chapter);
            chapterCheck(chapter);
        }
    }

    private void bookCheck(Book book) {
        assertNotNull("title null: " + book.getUrl(), book.getTitle());
        assertNotNull("catalog url null: " + book.getUrl(), book.getCatalogUrl());
        LOG("book", book.getTitle() + " " + book.getUrl() + "\n" + book.getCatalogUrl());
    }

    private void catalogCheck(List<Chapter> list) {
        StringBuilder sb = new StringBuilder();
        for (Chapter c : list) {
            sb.append(c.getIndex()).append(" ").append(c.getTitle()).append(" ").append(c.getUrl()).append("\n");
        }
        LOG("catalog", sb.toString());
    }

    private void chapterCheck(Chapter chapter) {
        assertNotNull("title null: " + chapter.getUrl(), chapter.getTitle());
        assertNotNull("content null: " + chapter.getUrl(), chapter.getContent());
        LOG("chapter", chapter.getTitle() + " " + chapter.getUrl() + "\n" + chapter.getContent());
    }
}