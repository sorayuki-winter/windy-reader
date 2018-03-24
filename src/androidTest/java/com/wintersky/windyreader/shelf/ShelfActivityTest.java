package com.wintersky.windyreader.shelf;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Created by tiandong on 18-3-24.
 * Shelf Test
 */
public class ShelfActivityTest {
    @Rule
    public ActivityTestRule<ShelfActivity> testRule =
            new ActivityTestRule<>(ShelfActivity.class);

    public CountDownLatch latch;

    @Before
    public void setUp() throws Exception {
        latch = new CountDownLatch(1);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void launch() throws Exception {
        latch.await();
    }
}