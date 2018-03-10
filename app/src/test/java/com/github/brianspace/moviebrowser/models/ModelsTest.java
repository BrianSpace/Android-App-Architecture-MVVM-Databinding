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

package com.github.brianspace.moviebrowser.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.brianspace.common.objstore.ModelObjectStore;
import com.github.brianspace.common.observable.ICollectionObserver;
import com.github.brianspace.common.observable.ICollectionObserver.Action;
import com.github.brianspace.moviebrowser.repository.IMovieDbService;
import com.github.brianspace.moviebrowser.repository.data.MovieData;
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Unit tests for model layer classes.
 * Note: Use Robolectric due to the dependency on {@link android.util.SparseArray}
 *       in {@link ModelObjectStore}.
 *       Another option is to implement a simple SparseArray in the test source tree.
 */
@SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts", "PMD.CommentRequired"})
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ModelsTest {
    // region Private Constants

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    // ----------- Values for Movie 1 -----------

    private static final int MOVIE1_ID = 1;
    private static final String MOVIE1_TITLE = "movie1Data";
    private static final String MOVIE1_POSTER_FILE = "poster.jpg";
    private static final String MOVIE1_JSON = "{\"id\": " + MOVIE1_ID + ", \"title\": \"" + MOVIE1_TITLE
            + "\", \"poster_path\": \"" + MOVIE1_POSTER_FILE + "\"}";

    // ----------- Values for Movie 2 -----------

    private static final int MOVIE2_ID = 2;
    private static final String MOVIE2_TITLE = "movie 2";
    private static final String MOVIE2_POSTER_FILE = "poster2.jpg";
    private static final String MOVIE2_JSON = "{\"id\": " + MOVIE2_ID + ", \"title\": \"" + MOVIE2_TITLE
            + "\", \"poster_path\": \"" + MOVIE2_POSTER_FILE + "\"}";

    // endregion

    // region Private Fields

    private static IMovieDbService service;
    private static EntityStore entityStore;

    private static MovieData movie1Data;
    private static MovieData movie2Data;

    private static PagingEnvelope<MovieData> movie2DataPage;

    private static List<MovieData> movieList = new ArrayList<>(1);

    // endregion

    // region Public Methods

    /**
     * Code to run after the test class is created.
     */
    @Before
    public void setUp() throws Exception {
        service = mock(IMovieDbService.class);
        entityStore = new EntityStore(service);

        movie1Data = GSON.fromJson(MOVIE1_JSON, MovieData.class);

        movie2Data = GSON.fromJson(MOVIE2_JSON, MovieData.class);
        movieList.clear();
        movieList.add(movie2Data);
        movie2DataPage = new PagingEnvelope<>(1, 1, 1, movieList);
    }

    @Test
    public void testMovie() throws Exception {
        final Movie movie = new Movie(service, entityStore, movie1Data);
        assertNotNull("Movie creation failed!", movie);
        verifyMovie1(movie);
    }

    @Test
    public void testSimilarMovies() throws Exception {
        when(service.getSimilarMovies(MOVIE1_ID, null)).thenReturn(Single.just(movie2DataPage));
        final Movie movie = new Movie(service, entityStore, movie1Data);

        final IMovieCollection similarMovies = movie.getSimilarMovies();
        assertNotNull("getSimilarMovies() should not return null!", similarMovies);
        testMovieList(similarMovies);
    }

    @Test
    public void testNowPlayingMovies() throws Exception {
        when(service.getMovieNowPlaying(null)).thenReturn(Single.just(movie2DataPage));

        final IMovieCollection nowPlayingMovies = new NowPlayingMovieCollection(service, entityStore);
        assertNotNull("Movie creation failed!", nowPlayingMovies);
        testMovieList(nowPlayingMovies);
    }

    // endregion

    // region Private Methods

    private void testMovieList(final IMovieCollection movies) throws InterruptedException {
        final ICollectionObserver collectionObserver = mock(ICollectionObserver.class);
        movies.addObserver(collectionObserver);

        assertNotNull("getMovies() should not return null!", movies.getMovies());
        assertFalse("isLoading should be false.", movies.isLoading());
        assertFalse("isLoaded should be false.", movies.isLoaded());
        final TestObserver<Void> testSubscriber = movies.load().test();
        testSubscriber.await();
        testSubscriber.assertNoErrors();

        assertFalse("isLoading should be false.", movies.isLoading());
        assertTrue("isLoaded should be true.", movies.isLoaded());
        assertFalse("hasNexPage should be false.", movies.hasNexPage());

        verify(collectionObserver).onUpdate(eq(movies), eq(Action.AppendRange), eq(null), anyList());

        final List<Movie> similarMoviesResult = movies.getMovies();
        assertNotNull("getMovies() should not return null after load()!", similarMoviesResult);
        verifyMovieList(similarMoviesResult);
    }

    private void verifyMovie1(final Movie movie) {
        assertEquals("Movie ID is not 1.", movie.getId(), MOVIE1_ID);
        assertEquals("Movie title does not match.", movie.getTitle(), MOVIE1_TITLE);
        assertEquals("Movie poster URL does not match.", movie.getPosterPath(), MOVIE1_POSTER_FILE);
        assertNotNull("getSimilarMovies() should never return null.", movie.getSimilarMovies());
    }

    private void verifyMovie2(final Movie movie) {
        assertEquals("Movie ID is not 1.", movie.getId(), MOVIE2_ID);
        assertEquals("Movie title does not match.", movie.getTitle(), MOVIE2_TITLE);
        assertEquals("Movie poster URL does not match.", movie.getPosterPath(), MOVIE2_POSTER_FILE);
        assertNotNull("getSimilarMovies() should never return null.", movie.getSimilarMovies());
    }

    private void verifyMovieList(final List<Movie> movieList) {
        assertFalse("Movie list is empty.", movieList.isEmpty());
        assertEquals("Movie list size is not one.", movieList.size(), 1);
        final Movie movie = movieList.get(0);
        verifyMovie2(movie);
    }

    // endregion
}