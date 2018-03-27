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

package com.github.brianspace.moviebrowser.repository.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.github.brianspace.moviebrowser.repository.data.MovieData
import com.github.brianspace.moviebrowser.repository.data.MovieDetailsData
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test cases for FavoriteStore.
 * [Testing Fundamentals](http://d.android.com/tools/testing/testing_android.html)
 * Note: due to the issue [https://issuetracker.google.com/issues/74489636], the test can only run by right click on the
 * test folder. See:
 * https://stackoverflow.com/questions/47969959/class-not-found-empty-test-suite-in-androidtest-using-android-studio-3-0-1-roo
 */
@RunWith(AndroidJUnit4::class)
class FavoriteStoreTest {

    /**
     * Database object.
     */
    private lateinit var db: FavoriteStore

    /**
     * Setup instrumentation context and Mockito.
     */
    @Before
    @Throws(Exception::class)
    fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        System.setProperty("dexmaker.dexcache", context.cacheDir.path) // Needed by Mockito
        // Setup test database
        db = FavoriteStore(context)
    }

    /**
     * Test database creation and reading.
     */
    @Test
    @Throws(Exception::class)
    fun testCreateRetrieve() {
        val id = 100
        val title = "Star Wars"
        val movie = MovieDetailsData()
        setMovieFields(movie, id, title)

        val testCreateObserver = db.addFavoriteMovie(movie).test()
        testCreateObserver.await().assertComplete().assertNoErrors()

        val testRetrieveObserver = db.getFavoriteMovie(id.toLong()).test()
        testRetrieveObserver.await().assertNoErrors().assertComplete()
        val result = testRetrieveObserver.values()
        assertNotNull("Result is empty!", result)
        val returnedMovie = result[0]
        assertNotNull("Returned list is empty!", returnedMovie)
        assertEquals("ID does not match!", returnedMovie.id.toLong(), id.toLong())
        assertEquals("Title does not match!", returnedMovie.title, title)
    }

    /**
     * Test database creation and reading.
     */
    @Test
    @Throws(Exception::class)
    fun testCreateRetrieveAll() {
        val id = 100
        val title = "Star Wars"
        val movie = MovieDetailsData()
        setMovieFields(movie, id, title)

        val testCreateObserver = db.addFavoriteMovie(movie).test()
        testCreateObserver.await().assertComplete().assertNoErrors()

        val testRetrieveAllObserver = db.allFavoriteMovies.test()
        testRetrieveAllObserver.await().assertNoErrors().assertComplete()
        assertNotNull("Result is empty!", testRetrieveAllObserver.values())
        val allResult = testRetrieveAllObserver.values()[0]
        assertNotNull("Result is empty!", allResult)
        assertNotNull("Returned list is empty!", allResult[0])
        assertEquals("ID does not match!", allResult[0].id.toLong(), id.toLong())
        assertEquals("Title does not match!", allResult[0].title, title)
    }

    @Throws(AssertionError::class)
    private fun setMovieFields(movie: MovieDetailsData, id: Int, title: String) {
        try {
            val fieldId = MovieData::class.java.getDeclaredField("id")
            fieldId.isAccessible = true
            fieldId.set(movie, id)

            val fieldTitle = MovieData::class.java.getDeclaredField("title")
            fieldTitle.isAccessible = true
            fieldTitle.set(movie, title)

        } catch (e: IllegalAccessException) {
            fail(e.message)
        } catch (e: NoSuchFieldException) {
            fail(e.message)
        }

    }
}
