package com.wintersky.windyreader.read;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static com.wintersky.windyreader.detail.DetailActivity.BOOK_URL;
import static com.wintersky.windyreader.read.ReadActivity.CHAPTER_URL;

/**
 * Created by tiandong on 18-3-26.
 */
public class ReadActivityTest {
    @Rule
    public ActivityTestRule<ReadActivity> testRule = new ActivityTestRule<ReadActivity>(ReadActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent();
            intent.putExtra(CHAPTER_URL, "http://zxzw.com/164588/14192209/");
            intent.putExtra(BOOK_URL, "http://zxzw.com/164588/");
            return intent;
        }
    };

    private CountDownLatch latch;

    @Before
    public void setUp() {
        latch = new CountDownLatch(1);
    }

    @Test
    public void launchRead() throws Exception {
        latch.await();
    }
}