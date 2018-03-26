package com.wintersky.windyreader.read;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static com.wintersky.windyreader.read.ReadActivity.EXTRA_CHAPTER_URL;
import static org.junit.Assert.*;

/**
 * Created by tiandong on 18-3-26.
 *
 */
public class ReadActivityTest {
    @Rule
    public ActivityTestRule<ReadActivity> testRule = new ActivityTestRule<ReadActivity>(ReadActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CHAPTER_URL, "http://www.8wenku.com/chapter/view?id=1498&chapter_no=5");
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
    public void launchRead() throws Exception {
        latch.await();
    }
}