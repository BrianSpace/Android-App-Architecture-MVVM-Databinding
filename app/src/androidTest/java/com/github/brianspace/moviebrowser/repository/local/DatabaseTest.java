/*
 * Copyright (C) 2018, Brian He
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.brianspace.moviebrowser.repository.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.github.brianspace.moviebrowser.repository.data.MovieData;
import com.github.brianspace.moviebrowser.repository.data.MovieDetailsData;
import io.reactivex.observers.TestObserver;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test cases for database.
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    /**
     * Database object.
     */
    private static FavoriteStore db;

    /**
     * Setup instrumentation context and Mockito.
     */
    @Before
    public void setUp() throws Exception {
        final Context context = InstrumentationRegistry.getTargetContext();
        System.setProperty("dexmaker.dexcache", context.getCacheDir().getPath()); // Needed by Mockito
        // Setup test database
        db = new FavoriteStore(context);
    }

    /**
     * Test database creation and reading.
     */
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void testCreateRetrieve() throws Exception {
        final int id = 100;
        final String title = "Star Wars";
        final MovieDetailsData movie = new MovieDetailsData();
        setMovieFields(movie, id, title);

        final TestObserver<Boolean> testCreateObserver = db.addFavoriteMovie(movie).test();
        testCreateObserver.await().assertComplete().assertNoErrors();

        final TestObserver<MovieData> testRetrieveObserver = db.getFavoriteMovie(id).test();
        testRetrieveObserver.await().assertNoErrors().assertComplete();
        final List<MovieData> result = testRetrieveObserver.values();
        assertNotNull("Result is empty!", result);
        final MovieData returnedMovie = result.get(0);
        assertNotNull("Returned list is empty!", returnedMovie);
        assertEquals("ID does not match!", returnedMovie.getId(), id);
        assertEquals("Title does not match!", returnedMovie.getTitle(), title);
    }

    /**
     * Test database creation and reading.
     */
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void testCreateRetrieveAll() throws Exception {
        final int id = 100;
        final String title = "Star Wars";
        final MovieDetailsData movie = new MovieDetailsData();
        setMovieFields(movie, id, title);

        final TestObserver<Boolean> testCreateObserver = db.addFavoriteMovie(movie).test();
        testCreateObserver.await().assertComplete().assertNoErrors();

        final TestObserver<List<MovieData>> testRetrieveAllObserver = db.getAllFavoriteMovies().test();
        testRetrieveAllObserver.await().assertNoErrors().assertComplete();
        assertNotNull("Result is empty!", testRetrieveAllObserver.values());
        final List<MovieData> allResult = testRetrieveAllObserver.values().get(0);
        assertNotNull("Result is empty!", allResult);
        assertNotNull("Returned list is empty!", allResult.get(0));
        assertEquals("ID does not match!", allResult.get(0).getId(), id);
        assertEquals("Title does not match!", allResult.get(0).getTitle(), title);
    }

    private void setMovieFields(final MovieDetailsData movie, final int id, final String title) throws AssertionError {
        try {
            final Field fieldId = MovieData.class.getDeclaredField("id");
            fieldId.setAccessible(true);
            fieldId.set(movie, id);

            final Field fieldTitle = MovieData.class.getDeclaredField("title");
            fieldTitle.setAccessible(true);
            fieldTitle.set(movie, title);

        } catch (final IllegalAccessException e) {
            fail(e.getMessage());
        } catch (final NoSuchFieldException e) {
            fail(e.getMessage());
        }
    }
}
