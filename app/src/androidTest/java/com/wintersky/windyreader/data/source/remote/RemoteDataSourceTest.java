package com.wintersky.windyreader.data.source.remote;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.util.SingleExecutors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.wintersky.windyreader.util.Constants.WS;
import static org.junit.Assert.assertNotNull;

public class RemoteDataSourceTest {

    private RemoteDataSource mSource;
    private String bookUrl;
    private String catalogUrl;
    private String chapterUrl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = InstrumentationRegistry.getTargetContext();
        mSource = new RemoteDataSource(context, new SingleExecutors());
        //*
        bookUrl = "http://www.8wenku.com/book/1871";
        catalogUrl = "http://www.8wenku.com/book/1871";
        chapterUrl = "http://www.8wenku.com/chapter/view?id=1871&chapter_no=1";
        //*/
        /*
        bookUrl = "http://zxzw.com/164718/";
        catalogUrl = "http://zxzw.com/164718/";
        chapterUrl = "http://zxzw.com/164718/14181340/";
        //*/
    }

    @Test
    public void getBook() throws Exception {
        Book book = mSource.getBookFromRemote(bookUrl);
        assertNotNull("book null", book);
        bookCheck(book);
    }

    @Test
    public void getCatalog() throws Exception {
        long st = System.currentTimeMillis();
        final List<Chapter> list = mSource.getCatalogFromRemote(catalogUrl);
        long et = System.currentTimeMillis();
        assertNotNull("chapter list null", list);
        catalogCheck(list);
        WS(String.format("cast: %d ms", et - st));
    }

    @Test
    public void getChapter() throws Exception {
        Chapter chapter = mSource.getChapterFromRemote(chapterUrl);
        assertNotNull("chapter null", chapter);
        chapterCheck(chapter);
    }

    private void bookCheck(Book book) {
        String s = "";
        s += book.getTitle() + " " + book.getUrl() + "\n";
        s += book.getCatalogUrl();
        WS(s);
    }

    private void catalogCheck(List<Chapter> list) {
        StringBuilder sb = new StringBuilder();
        for (Chapter c : list) {
            sb.append(c.getIndex()).append(" ").append(c.getTitle()).append(" ").append(c.getUrl()).append("\n");
        }
        WS(sb.toString());
    }

    private void chapterCheck(Chapter chapter) {
        WS("CU:" + chapter.getUrl() + "\nCT:" + chapter.getTitle() + "\nCC:\n" + chapter.getContent() + "\nEND CC");
        assertNotNull("content null", chapter.getContent());
        assertNotNull("title null", chapter.getTitle());
    }
}