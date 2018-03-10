package com.github.brianspace.widgets;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;

/**
 * Application tests.
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@SuppressWarnings("PMD.CommentRequired")
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void additionIsCorrect() throws AssertionError {
        assertEquals("Not equal.", 4, 2 + 2);
    }
}