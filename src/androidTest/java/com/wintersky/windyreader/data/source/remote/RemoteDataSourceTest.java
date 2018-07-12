package com.wintersky.windyreader.data.source.remote;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

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

    private RemoteDataSource mSource;
    private String chapterListUrl;
    private String chapterUrl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = InstrumentationRegistry.getTargetContext();
        LuaState lua = ComponentHolder.getAppComponent().getLuaState();
        mSource = new RemoteDataSource(context, new SingleExecutors(), lua);

        chapterListUrl = "http://zxzw.com/164588/";
        chapterUrl = "http://zxzw.com/164588/14192209/";
    }

    @Test
    public void searchBook() {

    }

    @Test
    public void getBook() {

    }

    @Test
    public void getCList() {
        final List<Chapter> list = mSource.getCListFromRemote(chapterListUrl);
        assertNotNull("chapter list null", list);
        chapterListCheck(list);
    }

    @Test
    public void getChapter() {
        Chapter chapter = mSource.getChapterFromRemote(chapterUrl);
        assertNotNull("chapter null", chapter);
        chapterCheck(chapter);
    }

    private void chapterListCheck(List<Chapter> list) {
        StringBuilder sb = new StringBuilder();
        for (Chapter c : list) {
            sb.append(c.getNum()).append(" ").append(c.getTitle()).append(" ").append(c.getUrl()).append("\n");
        }
        WS(sb.toString());
    }

    private void chapterCheck(Chapter chapter) {
        WS("CU:" + chapter.getUrl() + "\nCT:" + chapter.getTitle() + "\nCC:\n" + chapter.getContent() + "\nEND CC");
        assertNotNull("content null", chapter.getContent());
        assertNotNull("title null", chapter.getTitle());
    }
}