package com.wintersky.windyreader.search;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by tiandong on 18-3-24.
 * Search Test
 */
public class SearchActivityTest {
    @Rule
    public ActivityTestRule<SearchActivity> testRule = new ActivityTestRule<>(SearchActivity.class);

    private CountDownLatch latch;

    @Before
    public void setUp() {
        latch = new CountDownLatch(1);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void launchSearch() throws Exception {
        latch.await();
    }
}