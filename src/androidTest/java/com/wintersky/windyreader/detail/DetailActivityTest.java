package com.wintersky.windyreader.detail;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static com.wintersky.windyreader.detail.DetailActivity.EXTRA_BOOK_URL;
import static org.junit.Assert.*;

/**
 * Created by tiandong on 18-3-26.
 *
 */
public class DetailActivityTest {
    @Rule
    public ActivityTestRule<DetailActivity> testRule = new ActivityTestRule<DetailActivity>(DetailActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_BOOK_URL, "http://www.8wenku.com/book/1498");
            return intent;
        }
    };

    private CountDownLatch latch;

    @Before
    public void setUp() throws Exception {
        latch = new CountDownLatch(1);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void launchDetail() throws Exception {
        latch.await();
    }
}