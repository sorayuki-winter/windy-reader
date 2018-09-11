package com.wintersky.windyreader.data.source.remote;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.webkit.WebSettings;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.di.Component;
import com.wintersky.windyreader.util.SingleExecutors;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.wintersky.windyreader.util.CheckUtil.checkBook;
import static com.wintersky.windyreader.util.CheckUtil.checkCatalog;
import static com.wintersky.windyreader.util.LogUtil.LOG;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RemoteDataSourceTest {

    private Context mContext;
    private OkHttpClient mHttp;
    private RemoteDataSource mSource;
    private List<String> bookList;
    private List<String> catalogList;
    private List<String> chapterList;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        mHttp = Component.get().getOkHttpClient();
        mSource = new RemoteDataSource(mContext, new SingleExecutors(), mHttp);

        bookList = new ArrayList<>();
        catalogList = new ArrayList<>();
        chapterList = new ArrayList<>();

        bookList.add("http://www.8wenku.com/book/1871");
        catalogList.add("http://www.8wenku.com/book/1871");
        chapterList.add("http://www.8wenku.com/chapter/view?id=1871&chapter_no=1");

        bookList.add("http://www.8wenku.com/book/3030");
        catalogList.add("http://www.8wenku.com/book/3030");
        chapterList.add("http://www.8wenku.com/chapter/view?id=3030&chapter_no=1");

        bookList.add("http://zxzw.com/164718/");
        catalogList.add("http://zxzw.com/164718/");
        chapterList.add("http://zxzw.com/164718/14181340/");
    }

    @Test
    public void remoteTest() throws IOException {
        Request get = new Request.Builder().url("http://www.8wenku.com")
                .addHeader("User-Agent", WebSettings.getDefaultUserAgent(mContext))
                .build();
        Response response = mHttp.newCall(get).execute();
        assertTrue(response.isSuccessful());
        ResponseBody body = response.body();
        assertNotNull(body);
    }

    @Test
    public void remoteGetBookFrom() throws Exception {
        for (String url : bookList) {
            Book book = mSource.getBookFrom(url);
            assertNotNull("book null: " + url, book);
            checkBook(book);
        }
    }

    @Test
    public void remoteGetCatalogFrom() throws Exception {
        for (String url : catalogList) {
            long st = System.currentTimeMillis();
            final List<Chapter> list = mSource.getCatalogFrom(url);
            long et = System.currentTimeMillis();
            assertNotNull("chapter list null: " + url, list);
            checkCatalog(list);
            LOG(String.format("Cast: %d ms", et - st));
        }
    }

    @Test
    public void remoteGetContentFrom() throws Exception {
        for (String url : chapterList) {
            String content = mSource.getContentFrom(url);
            assertNotNull("content null: " + url, content);
            LOG("Content: " + url, content);
        }
    }
}