package com.wintersky.windyreader.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.util.SingleExecutors;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.wintersky.windyreader.util.CheckUtil.checkBook;
import static com.wintersky.windyreader.util.CheckUtil.checkCatalog;
import static com.wintersky.windyreader.util.CheckUtil.checkChapter;
import static com.wintersky.windyreader.util.CheckUtil.checkShelf;
import static com.wintersky.windyreader.util.LogUtil.LOG;
import static com.wintersky.windyreader.util.LogUtil.LOGD;
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
        Context context = InstrumentationRegistry.getTargetContext();
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("test.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        mRealm = Realm.getInstance(config);
        mSource = new LocalDataSource(context, new SingleExecutors(), mRealm);

        mChapter = new Chapter();
        mChapter.setTitle("CHAPTER");
        mChapter.setUrl("http://chapter/0");
        mContent = "最新最全的日本动漫轻小说 轻小说文库(http://www.8wenku.com) 为你一网打尽！\n\n第一卷 序章 某洞窟里的事件\n\n台版 转自 轻小说文库\n\n图源：kerorokun\n\n扫图：a8901566\n\n录入：a8901566\n\n人类全都是混帐。\n\n我蜷缩在洞窟深处，忍著痛楚无止息的煎熬……没完没了地埋怨著、诅咒著。\n\n——或者说，也只能这么做。\n\n这副疲劳过度的身躯早已疲惫不堪到——连意识都无法顺利传达。\n\n饥饿已久的胃像是要被胃酸蚀穿，晕眩戚震荡著脑袋里的一切。\n\n呼吸也既炽热又不稳定。要是我真的得了什么病的话，这下更是来日无多了。\n\n不得不承认，我现在正陷于穷途末路的生死关头。\n\n……可恶、可恶！我竟然会在这种陌生的地方，一个人孤单等死。\n\n我究竟是犯了什么罪，非得落得这种下场？\n\n不，不对。\n\n我根本什么也没做。错的是那些家伙，我只不过是个受害者！\n\n就这样，我不断怨天尤人著。\n\n要是不这么做的话，恐怕无法维持住即将消逝的意识。\n\n也许这份憎恨消失的瞬间，我也将从这世上消失吧。\n\n但命运像是在嘲笑我的这些努力般，洞口处就在这时传来声响——某种东西拖行著，发出连灵魂都能削蚀般的不吉祥摩擦声。\n\n……别过来。别过来别过来别过来！\n\n我在心中大吼大叫，但那声音却逐步逼近。\n\n不行了，我再也逃不动了……\n\n抱著绝望蜷曲的我，望著声响传来的方向。\n\n在眼前的，是拥有半液态体组织，高度超过两公尺的怪物。\n\n「啊啊……」\n\n怪物——这是人们对它的称呼，也是人类的天敌。\n\n眼前这只通称为『史莱姆』的怪物明明没有眼珠，却还是找到了我……并以跟外观毫不相称的敏捷速度直逼而来。\n\n我无路可逃了。或者该说，我早就累到站都站不起来。\n\n「可恶——」\n\n它先用强烈的消化液吞噬了我瘫软的手臂，但肌肤受侵蚀的痛楚并未直达我精疲力竭的大脑，取而代之的是一股麻痹与知觉丧失感。啊啊，看来我的生命就到此为止了吧。\n\n不要、不要、不要——我不要这样子！\n\n「……谁来……救救我……」\n\n撇下一句不争气的遗言，我就这么舍弃了意识。\n\n这是我对人性感到绝望后，第三天早上发生的事。 \n\n1.0001843000184;9999999\n\n本文来自 轻小说文库(http://www.8wenku.com)";

        RealmList<Chapter> catalog = new RealmList<>();
        catalog.add(mChapter);

        mBook = new Book();
        mBook.setTitle("BOOK");
        mBook.setUrl("http://book");
        mBook.setCatalogUrl("http://chapter");
        mBook.setCatalog(catalog);

        localSaveBook();
    }

    @Test
    public void tearDown() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.deleteAll();
            }
        });
    }

    @Test
    public void clearUselessChapter() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<Book> books = realm.where(Book.class).findAll();
                final List<String> catalogUrls = new ArrayList<>();
                for (Book bk : books.createSnapshot()) {
                    catalogUrls.add(bk.catalogUrl);
                }
                final RealmResults<Chapter> chapters = realm.where(Chapter.class)
                        .findAll();
                final RealmResults<Chapter> del = realm.where(Chapter.class)
                        .not().in("catalogUrl", catalogUrls.toArray(new String[]{}))
                        .findAll();
                LOGD(String.format("del %d in %d", del.size(), chapters.size()));
                if (!del.isEmpty()) {
                    assertTrue(del.deleteAllFromRealm());
                }
            }
        });
        realm.close();
    }

    @Test
    public void localGetShelf() {
        mSource.getShelf(new DataSource.GetShelfCallback() {
            @Override
            public void onLoaded(@NonNull RealmResults<Book> list) {
                checkShelf(list);
            }
        });
    }

    @Test
    public void localGetBook() {
        mSource.getBook(mBook.getUrl(), new DataSource.GetBookCallback() {
            @Override
            public void onLoaded(@NonNull Book book) {
                checkBook(book);
            }

            @Override
            public void onFailed(@NonNull Exception e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void localGetCatalog() {
        mSource.getCatalog(mBook.getUrl(), new DataSource.GetCatalogCallback() {
            @Override
            public void onLoaded(@NonNull List<Chapter> list) {
                checkCatalog(list);
            }

            @Override
            public void onFailed(@NonNull Exception e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void localGetContent() {
        mSource.getChapter(mChapter.getUrl(), new DataSource.GetChapterCallback() {
            @Override
            public void onLoaded(@NonNull Chapter chapter) {
                checkChapter(chapter);
            }

            @Override
            public void onFailed(@NonNull Exception e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void localSaveBook() {
        mSource.saveBook(mBook, new DataSource.SaveBookCallback() {
            @Override
            public void onSaved(@NonNull Book book) {

            }

            @Override
            public void onFailed(@NonNull Exception e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void localDeleteBook() {
        mSource.deleteBook(mBook.getUrl());
    }

    @Test
    public void localSaveContentTo() {
        assertTrue(mSource.saveContentTo(mChapter.getUrl(), mContent));
    }

    @Test
    public void localGetContentFrom() throws Exception {
        String content = mSource.getContentFrom(mChapter.getUrl());
        assertNotNull(content);
        LOG("content", content.replace("\n", "\\n"));
    }
}
