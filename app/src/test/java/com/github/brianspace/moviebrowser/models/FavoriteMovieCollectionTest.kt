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

package com.github.brianspace.moviebrowser.models

import com.github.brianspace.common.objstore.ModelObjectStore
import com.github.brianspace.common.observable.ICollectionObserver
import com.github.brianspace.common.observable.ICollectionObserver.Action
import com.github.brianspace.common.observable.IObserver
import com.github.brianspace.moviebrowser.repository.IFavoriteStore
import com.github.brianspace.moviebrowser.repository.IMovieDbService
import com.github.brianspace.moviebrowser.repository.data.MovieData
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// region Private Constants

private val GSON = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create()

// ----------- Values for Movie 1 -----------

private const val MOVIE1_ID = 1
private const val MOVIE1_TITLE = "movie1Data"
private const val MOVIE1_POSTER_FILE = "poster.jpg"
private const val MOVIE1_JSON = ("{\"id\": " + MOVIE1_ID + ", \"title\": \"" + MOVIE1_TITLE
        + "\", \"poster_path\": \"" + MOVIE1_POSTER_FILE + "\"}")

// ----------- Values for Movie 2 -----------

private const val MOVIE2_ID = 2
private const val MOVIE2_TITLE = "movie 2"
private const val MOVIE2_POSTER_FILE = "poster2.jpg"
private const val MOVIE2_JSON = ("{\"id\": " + MOVIE2_ID + ", \"title\": \"" + MOVIE2_TITLE
        + "\", \"poster_path\": \"" + MOVIE2_POSTER_FILE + "\"}")

// endregion

/**
 * Unit tests for model layer class [FavoriteMovieCollection].
 * Note: Use Robolectric due to the dependency on [android.util.SparseArray]
 * in [ModelObjectStore].
 * Another option is to implement a simple SparseArray in the test source tree.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class FavoriteMovieCollectionTest {

    // region Private Fields

    private val service: IMovieDbService = mock()
    private val entityStore: EntityStore

    private val movie1Data: MovieData = GSON.fromJson(MOVIE1_JSON, MovieData::class.java)
    private val movieList: List<MovieData>

    // endregion

    // region Public Methods

    init {
        entityStore = EntityStore(service)

        val movie2Data = GSON.fromJson(MOVIE2_JSON, MovieData::class.java)
        movieList = listOf(movie2Data)
    }

    @Test
    @Throws(Exception::class)
    fun testFavoriteMovies() {
        val favoriteStore = mock<IFavoriteStore> {
            on { allFavoriteMovies } doReturn Single.just(movieList)
        }

        val favoriteMovies = FavoriteMovieCollection(favoriteStore, entityStore)
        assertNotNull("FavoriteMovieCollection creation failed!", favoriteMovies)
        testMovieList(favoriteMovies)
    }

    @Test
    @Throws(Exception::class)
    fun testAddFavoriteMovie() {
        val favoriteStore = mock<IFavoriteStore> {
            on { addFavoriteMovie(any()) } doReturn Single.just(true)
        }
        val favorite = FavoriteMovieCollection(favoriteStore, entityStore)

        val collectionObserver = mock<ICollectionObserver>()
        favorite.addObserver(collectionObserver)

        val movie = Movie(service, entityStore, movie1Data)
        assertFalse("isFavorite should be false.", movie.isFavorite)
        assertNotNull("getMovies() should not return null!", favorite.movies)
        assertTrue("Favorite movie list should be empty.", favorite.movies.isEmpty())

        val objectObserver = mock<IObserver>()
        movie.addObserver(objectObserver)

        val testSubscriber = favorite.addToFavorite(movie).test()
        testSubscriber.await()
        testSubscriber.assertNoErrors()

        verify(favoriteStore).addFavoriteMovie(eq(movie1Data))
        verify(collectionObserver).onUpdate(
            eq<IFavoriteMovieCollection>(favorite), eq(Action.AddItemToFront),
            eq(movie), eq<List<Any>?>(null)
        )
        verify(objectObserver).onUpdate(eq(movie), anyOrNull())

        assertTrue("isFavorite should be false.", movie.isFavorite)
        assertEquals(
            "Favorite movie list should contain exact one item.",
            1, favorite.movies.size.toLong()
        )
        verifyMovie1(favorite.movies[0])
    }

    // endregion

    // region Private Methods

    @Throws(InterruptedException::class)
    private fun testMovieList(movies: IMovieCollection) {
        val collectionObserver = mock<ICollectionObserver>()
        movies.addObserver(collectionObserver)

        assertNotNull("getMovies() should not return null!", movies.movies)
        assertFalse("isLoading should be false.", movies.isLoading)
        assertFalse("isLoaded should be false.", movies.isLoaded)
        val testSubscriber = movies.load().test()
        testSubscriber.await()
        testSubscriber.assertNoErrors()

        assertFalse("isLoading should be false.", movies.isLoading)
        assertTrue("isLoaded should be true.", movies.isLoaded)
        assertFalse("hasNexPage should be false.", movies.hasNexPage())

        verify(collectionObserver).onUpdate(eq(movies), eq(Action.AppendItem), any(), eq<List<Any>?>(null))

        val similarMoviesResult = movies.movies
        assertNotNull("getMovies() should not return null after load()!", similarMoviesResult)
        verifyMovieList(similarMoviesResult)
    }

    private fun verifyMovie1(movie: Movie) {
        assertEquals("Movie ID is not 1.", movie.id.toLong(), MOVIE1_ID.toLong())
        assertEquals("Movie title does not match.", movie.title, MOVIE1_TITLE)
        assertEquals("Movie poster URL does not match.", movie.posterPath, MOVIE1_POSTER_FILE)
        assertNotNull("getSimilarMovies() should never return null.", movie.getSimilarMovies())
    }

    private fun verifyMovie2(movie: Movie) {
        assertEquals("Movie ID is not 1.", movie.id.toLong(), MOVIE2_ID.toLong())
        assertEquals("Movie title does not match.", movie.title, MOVIE2_TITLE)
        assertEquals("Movie poster URL does not match.", movie.posterPath, MOVIE2_POSTER_FILE)
        assertNotNull("getSimilarMovies() should never return null.", movie.getSimilarMovies())
    }

    private fun verifyMovieList(movieList: List<Movie>) {
        assertFalse("Movie list is empty.", movieList.isEmpty())
        assertEquals("Movie list size is not one.", movieList.size.toLong(), 1)
        val movie = movieList[0]
        verifyMovie2(movie)
    }

    // endregion
}
