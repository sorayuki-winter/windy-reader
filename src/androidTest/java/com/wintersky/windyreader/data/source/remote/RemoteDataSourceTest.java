package com.wintersky.windyreader.data.source.remote;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.di.ComponentHolder;
import com.wintersky.windyreader.util.SingleExecutors;

import org.junit.Before;
import org.junit.Test;
import org.keplerproject.luajava.LuaState;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.wintersky.windyreader.util.Constants.WS;
import static org.junit.Assert.assertNotNull;

public class RemoteDataSourceTest {

    private RemoteDataSource dataSource;

    private String searchUrl;
    private String bookUrl;
    private String chapterListUrl;
    private String chapterUrl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = InstrumentationRegistry.getTargetContext();
        LuaState lua = ComponentHolder.getAppComponent().getLuaState();
        dataSource = new RemoteDataSource(context, new SingleExecutors(), lua);

        searchUrl = "";
        bookUrl = "http://zxzw.com/164588/";
        chapterListUrl = "http://zxzw.com/164588/";
        chapterUrl = "http://zxzw.com/164588/14192209/";
    }

    @Test
    public void searchBook() {
        List<Book> list = dataSource.searchBookFromRemote(searchUrl, "约会");
        assertNotNull("book list null", list);
        searchCheck(list);
    }

    @Test
    public void getBook() {
        Book book = dataSource.getBookFromRemote(bookUrl);
        assertNotNull("book null", book);
        bookCheck(book);
    }

    @Test
    public void getChapterList() {
        List<Chapter> list = dataSource.getChapterListFromRemote(chapterListUrl);
        assertNotNull("chapter list null", list);
        chapterListCheck(list);
    }

    @Test
    public void getChapter() {
        Chapter chapter = dataSource.getChapterFromRemote(chapterUrl);
        assertNotNull("chapter null", chapter);
        chapterCheck(chapter);
    }

    private void searchCheck(List<Book> list) {
        for (Book bk : list) {
            WS(bk.title + " " + bk.url);
        }
    }

    private void bookCheck(Book book) {
        WS("BU:" + book.url + "TT:" + book.title + "IU:" + book.imgUrl + "CL:" + book.chapterListUrl + "AT:" + book.author + "DT:" + book.detail);
        assertNotNull("title null", book.title);
        assertNotNull("imgUrl null", book.imgUrl);
        assertNotNull("chapterListUrl null", book.chapterListUrl);
        assertNotNull("author null", book.author);
        assertNotNull("detail null", book.detail);
    }

    private void chapterListCheck(List<Chapter> list) {
        StringBuilder sb = new StringBuilder();
        for (Chapter c : list) {
            sb.append(c.getId()).append(" ").append(c.title).append(" ").append(c.url).append("\n");
        }
        WS(sb.toString());
    }

    private void chapterCheck(Chapter chapter) {
        WS("CU:" + chapter.url + "\nCT:" + chapter.title + "\nCC:\n" + chapter.content + "\nEND CC");
        assertNotNull("content null", chapter.content);
        assertNotNull("title null", chapter.title);
    }
}