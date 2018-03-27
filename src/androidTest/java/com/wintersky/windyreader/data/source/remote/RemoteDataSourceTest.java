package com.wintersky.windyreader.data.source.remote;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.di.ComponentHolder;
import com.wintersky.windyreader.util.SingleExecutors;

import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.Test;
import org.keplerproject.luajava.LuaState;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wintersky.windyreader.util.Constants.LT;
import static org.junit.Assert.assertNotNull;

public class RemoteDataSourceTest {

    private RemoteDataSource dataSource;
    private LuaState mLuaState;

    private Context mContext;

    private String searchUrl;
    private String bookUrl;
    private String chapterListUrl;
    private String chapterUrl;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Context context = InstrumentationRegistry.getTargetContext();
        mLuaState = ComponentHolder.getAppComponent().getLuaState();
        dataSource = new RemoteDataSource(context, new SingleExecutors(), mLuaState);

        mContext = InstrumentationRegistry.getContext();

        //OVERLORD
        searchUrl = "http://www.8wenku.com/site/search";
        bookUrl = "http://www.8wenku.com/book/1498";
        chapterListUrl = "http://www.8wenku.com/book/1498";
        chapterUrl = "http://www.8wenku.com/chapter/view?id=1498&chapter_no=5";

        /*
        //元尊
        searchUrl = ""
        bookUrl = "https://www.bxwx9.org/binfo/5/5740.htm";
        chaptersUrl = "https://www.bxwx9.org/b/5/5740/index.html";
        chapterUrl = "https://www.bxwx9.org/b/5/5740/41209550.html";
        */
    }

    @Test
    public void searchBook() throws Exception {
        loadTestFile(searchUrl, "s.html");
        List<Book> list = dataSource.searchBookFromRemote(searchUrl, "约会");
        assertNotNull("book list null", list);
        searchCheck(list);
    }

    @Test
    public void getBook() throws Exception {
        loadTestFile(bookUrl, "b.html");
        Book book = dataSource.getBookFromRemote(bookUrl);
        assertNotNull("book null", book);
        bookCheck(book);
    }

    @Test
    public void getChapterList() throws Exception {
        loadTestFile(chapterListUrl, "cl.html");
        List<Chapter> list = dataSource.getChapterListFromRemote(chapterListUrl);
        assertNotNull("chapter list null", list);
        chapterListCheck(list);
    }

    @Test
    public void getChapter() throws Exception {
        loadTestFile(chapterUrl, "c.html");
        Chapter chapter = dataSource.getChapterFromRemote(chapterUrl);
        assertNotNull("chapter null", chapter);
        chapterCheck(chapter);
    }

    private void searchCheck(List<Book> list) {
        for (Book bk : list) {
            Log.i(LT, bk.title + " " + bk.url);
        }
    }

    private void bookCheck(Book book) {
        Log.i(LT, "BU:" + book.url);
        Log.i(LT, "TT:" + book.title);
        Log.i(LT, "IU:" + book.imgUrl);
        Log.i(LT, "CL:" + book.chapterListUrl);
        Log.i(LT, "AT:" + book.author);
        Log.i(LT, "DT:" + book.detail);
        assertNotNull("title null", book.title);
        assertNotNull("imgUrl null", book.imgUrl);
        assertNotNull("chapterListUrl null", book.chapterListUrl);
        assertNotNull("author null", book.author);
        assertNotNull("detail null", book.detail);
    }

    private void chapterListCheck(List<Chapter> list) {
        for (Chapter it : list) {
            Log.i(LT, it.title + " " + it.url);
        }
    }

    private void chapterCheck(Chapter chapter) {
        Log.i(LT, "CU:" + chapter.url);
        Log.i(LT, "TT:" + chapter.title);
        Log.i(LT, "CT:" + chapter.content);
        assertNotNull("content null", chapter.content);
        assertNotNull("title null", chapter.title);
    }

    private void loadTestFile(String url, String file) throws Exception {
        Matcher m = Pattern.compile("(?<=\\.)[^.]+").matcher(url);
        String dir = "";
        if (m.find()) dir += m.group() + "/";
        org.jsoup.nodes.Document doc = Jsoup.parse(mContext.getAssets().open(dir + file),
                "UTF-8", url);
        mLuaState.pushJavaObject(doc);
        mLuaState.setGlobal("__doc");
    }
}