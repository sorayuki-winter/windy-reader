package com.wintersky.windyreader;

import org.junit.Test;

import static com.wintersky.windyreader.util.LogUtil.LOG;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void log() {
        LOG("Hint", "Log");
        LOG("Log");
    }
}