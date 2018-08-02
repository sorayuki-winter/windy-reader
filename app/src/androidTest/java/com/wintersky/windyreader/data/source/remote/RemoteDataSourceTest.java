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

        bookUrl = "http://zxzw.com/164588/";
        catalogUrl = "http://zxzw.com/164588/";
        chapterUrl = "http://zxzw.com/26133/2478957/";
    }

    @Test
    public void getBook() {
        Book book = mSource.getBookFromRemote(bookUrl);
        assertNotNull("book null", book);
        bookCheck(book);
    }

    @Test
    public void getCatalog() {
        final List<Chapter> list = mSource.getCatalogFromRemote(catalogUrl);
        assertNotNull("chapter list null", list);
        catalogCheck(list);
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