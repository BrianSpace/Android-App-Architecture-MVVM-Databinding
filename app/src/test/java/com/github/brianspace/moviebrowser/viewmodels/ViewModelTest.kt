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

package com.github.brianspace.moviebrowser.viewmodels

import android.databinding.ObservableList
import com.github.brianspace.common.objstore.ModelObjectStore
import com.github.brianspace.common.observable.ICollectionObserver
import com.github.brianspace.common.observable.ICollectionObserver.Action
import com.github.brianspace.moviebrowser.models.*
import com.github.brianspace.moviebrowser.repository.IMovieDbService
import com.github.brianspace.moviebrowser.repository.data.MovieData
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

// region Private Constants

private const val DEFAULT_IMAGE_BASE_URL = "http://a.b.c/w500/"
private val GSON = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

// ----------- Values for Movie 1 -----------

private const val MOVIE1_ID = 100
private const val MOVIE1_TITLE = "movie1Data"
private const val MOVIE1_POSTER_FILE = "poster.jpg"
private const val MOVIE1_POSTER_URL = DEFAULT_IMAGE_BASE_URL + MOVIE1_POSTER_FILE
private const val MOVIE1_JSON = ("{\"id\": " + MOVIE1_ID + ", \"title\": \"" + MOVIE1_TITLE
        + "\", \"poster_path\": \"" + MOVIE1_POSTER_FILE + "\"}")

// endregion

/**
 * Unit test cases for view models.
 * Note: Use Robolectric due to the dependency on [android.util.SparseArray] in [ModelObjectStore],
 * as well as [io.reactivex.android.schedulers.AndroidSchedulers].
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ViewModelTest {

    // region Private Properties

    private val movie1Data: MovieData = GSON.fromJson(MOVIE1_JSON, MovieData::class.java)

    private val movieDataList : List<MovieData> = listOf(movie1Data)
    private val movie1DataPage: PagingEnvelope<MovieData> = PagingEnvelope(1, 1, 1, movieDataList)

    private val imageConfig: IImageConfig = mock {
        on {getPosterBaseUrl(anyInt())} doReturn DEFAULT_IMAGE_BASE_URL
    }

    private val service: IMovieDbService
    private val entityStore: EntityStore

    private val movie1: Movie

    // Movie list & collection which contains movie1.
    private val movieList1 : List<Movie>
    private val movieCollection1: IMovieCollection

    private val favoriteMovieCollection: IFavoriteMovieCollection
    private val viewModelFactory: ViewModelFactory

    // endregion

    // region Public Methods

    init {
        service = mock {
            on {getSimilarMovies(MOVIE1_ID, null)} doReturn Single.just(movie1DataPage)
        }

        entityStore = EntityStore(service)

        movie1 = Movie(service, entityStore, movie1Data)

        movieList1 = listOf(movie1)

        movieCollection1 = mock {
            on {load()} doReturn Completable.complete()
            on {movies} doReturn movieList1
        }

        favoriteMovieCollection = mock {
            on {movies} doReturn movieList1
        }

        viewModelFactory = ViewModelFactory(imageConfig, entityStore, favoriteMovieCollection)
    }

    /**
     * Test the view model of a movie.
     */
    @Test
    @Throws(Exception::class)
    fun testMovieViewModel() {
        val movieViewModel = MovieViewModel(movie1, imageConfig, favoriteMovieCollection)
        assertNotNull("Should not be null.", movieViewModel)
        verifyMovie1ViewModel(movieViewModel)
    }


    /**
     * Test the view model of a movie details.
     */
    @Test
    @Throws(Exception::class)
    fun testMovieDetailsViewModel() {
        val movieViewModel = MovieDetailsViewModel(movie1, viewModelFactory, imageConfig, favoriteMovieCollection)
        assertNotNull("Should not be null.", movieViewModel)
        verifyMovie1DetailsViewModel(movieViewModel)
    }

    /**
     * Test the view model of movie list.
     */
    @Test
    @Throws(Exception::class)
    fun testMoviesViewModel() {
        val observerCaptor = argumentCaptor<ICollectionObserver>()
        val moviesViewModel = MoviesViewModel(movieCollection1, viewModelFactory)
        // Capture the observer.
        verify(movieCollection1).addObserver(observerCaptor.capture())

        val testObserver = moviesViewModel.load().test()
        testObserver.awaitDone(5, TimeUnit.SECONDS)
        testObserver.assertNoErrors()

        assertEquals("Movie list should be empty before change events are fired.",
                0, moviesViewModel.movies.size.toLong())

        // Notify the view model for the change.
        observerCaptor.firstValue.onUpdate(movieCollection1, Action.AddItemToFront, movie1, null)

        // Verify the list after change events are notified.
        verifyList1(moviesViewModel.movies)
    }

    // endregion

    // region Private Methods

    private fun verifyList1(movieViewModelList: ObservableList<MovieViewModel>) {
        assertFalse("Movie list is empty.", movieViewModelList.isEmpty())
        assertEquals("Movie list size is not one.", movieViewModelList.size.toLong(), 1)
        val movieViewModel = movieViewModelList[0]
        verifyMovie1ViewModel(movieViewModel)
    }

    private fun verifyMovie1ViewModel(movieViewModel: MovieViewModel) {
        assertEquals("Movie title does not match.", movieViewModel.title, MOVIE1_TITLE)
        assertEquals("Movie poster URL does not match.",
                movieViewModel.getPosterUrl(100), MOVIE1_POSTER_URL)
    }

    private fun verifyMovie1DetailsViewModel(movieDetailsViewModel: MovieDetailsViewModel) {
        assertEquals("Movie title does not match.", movieDetailsViewModel.title, MOVIE1_TITLE)
        assertEquals("Movie poster URL does not match.",
                movieDetailsViewModel.getPosterUrl(100), MOVIE1_POSTER_URL)
        assertNotNull("Similar movie list should not be null.", movieDetailsViewModel.similarMovies)
    }
}
