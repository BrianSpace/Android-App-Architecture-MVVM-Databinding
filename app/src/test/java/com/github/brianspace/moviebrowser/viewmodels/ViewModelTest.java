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

package com.github.brianspace.moviebrowser.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.databinding.ObservableList;
import com.github.brianspace.common.objstore.ModelObjectStore;
import com.github.brianspace.common.observable.ICollectionObserver;
import com.github.brianspace.common.observable.ICollectionObserver.Action;
import com.github.brianspace.moviebrowser.models.IEntityStore;
import com.github.brianspace.moviebrowser.models.IFavoriteMovieCollection;
import com.github.brianspace.moviebrowser.models.IImageConfig;
import com.github.brianspace.moviebrowser.models.IMovieCollection;
import com.github.brianspace.moviebrowser.models.Movie;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Unit test cases for view models.
 * Note: Use Robolectric due to the dependency on {@link android.util.SparseArray}
 *       in {@link ModelObjectStore},
 *       as well as {@link io.reactivex.android.schedulers.AndroidSchedulers}.
 */
@SuppressWarnings("PMD.CommentRequired")
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ViewModelTest {

    // region Private Constants

    private static final String DEFAULT_IMAGE_BASE_URL = "http://a.b.c/w500/";

    // ----------- Values for Movie 1 -----------

    private static final int MOVIE1_ID = 100;
    private static final String MOVIE1_TITLE = "movie1Data";
    private static final String MOVIE1_POSTER_FILE = "poster.jpg";
    private static final String MOVIE1_POSTER_URL = DEFAULT_IMAGE_BASE_URL + MOVIE1_POSTER_FILE;

    // ----------- Values for Movie 2 -----------

    private static final int MOVIE2_ID = 200;
    private static final String MOVIE2_TITLE = "movie 2";
    private static final String MOVIE2_POSTER_FILE = "poster2.jpg";

    // endregion

    // region Private Fields

    private Movie movie1;

    // Movie list & collection which contains movie1.
    private final List<Movie> movieList1 = new ArrayList<>(1);
    private IMovieCollection movieCollection1;

    // Movie list & collection which contains movie2.
    private final List<Movie> movieList2 = new ArrayList<>(1);

    private IImageConfig imageConfig;
    private IFavoriteMovieCollection favoriteMovieCollection;
    private ViewModelFactory viewModelFactory;

    // endregion

    // region Public Methods

    /**
     * Code to run after the test class is created.
     */
    @SuppressWarnings("PMD.NcssCount")
    @Before
    public void setUp() throws Exception {
        imageConfig = mock(IImageConfig.class);
        when(imageConfig.getPosterBaseUrl(anyInt())).thenReturn(DEFAULT_IMAGE_BASE_URL);

        movie1 = mock(Movie.class);
        movieCollection1 = mock(IMovieCollection.class);

        final Movie movie2 = mock(Movie.class);
        final IMovieCollection movieCollection2 = mock(IMovieCollection.class);

        when(movie1.getId()).thenReturn(MOVIE1_ID);
        when(movie1.getTitle()).thenReturn(MOVIE1_TITLE);
        when(movie1.getPosterPath()).thenReturn(MOVIE1_POSTER_FILE);
        when(movie1.getSimilarMovies()).thenReturn(movieCollection2);

        movieList1.clear();
        movieList1.add(movie1);

        when(movieCollection1.load()).thenReturn(Completable.complete());
        when(movieCollection1.getMovies()).thenReturn(movieList1);

        when(movie2.getId()).thenReturn(MOVIE2_ID);
        when(movie2.getTitle()).thenReturn(MOVIE2_TITLE);
        when(movie2.getPosterPath()).thenReturn(MOVIE2_POSTER_FILE);
        when(movie2.getSimilarMovies()).thenReturn(movieCollection1);

        movieList2.clear();
        movieList2.add(movie2);

        when(movieCollection2.getMovies()).thenReturn(movieList2);

        final IEntityStore entityStore = mock(IEntityStore.class);

        favoriteMovieCollection = mock(IFavoriteMovieCollection.class);
        when(favoriteMovieCollection.getMovies()).thenReturn(movieList1);

        viewModelFactory = new ViewModelFactory(imageConfig, entityStore, favoriteMovieCollection);
    }

    /**
     * Test the view model of a movie.
     */
    @Test
    public void testMovieViewModel() throws Exception {
        final MovieViewModel movieViewModel = new MovieViewModel(movie1, imageConfig, favoriteMovieCollection);
        assertNotNull("Should not be null.", movieViewModel);
        verifyMovie1ViewModel(movieViewModel);
    }


    /**
     * Test the view model of a movie details.
     */
    @Test
    public void testMovieDetailsViewModel() throws Exception {
        final MovieDetailsViewModel movieViewModel =
                new MovieDetailsViewModel(movie1, viewModelFactory, imageConfig, favoriteMovieCollection);
        assertNotNull("Should not be null.", movieViewModel);
        verifyMovie1DetailsViewModel(movieViewModel);
    }

    /**
     * Test the view model of movie list.
     */
    @Test
    public void testMoviesViewModel() throws Exception {
        final ArgumentCaptor<ICollectionObserver> observerCaptor = ArgumentCaptor.forClass(ICollectionObserver.class);
        final MoviesViewModel moviesViewModel = new MoviesViewModel(movieCollection1, viewModelFactory);
        // Capture the observer.
        verify(movieCollection1).addObserver(observerCaptor.capture());

        final TestObserver<Void> testObserver = moviesViewModel.load().test();
        testObserver.awaitDone(5, TimeUnit.SECONDS);
        testObserver.assertNoErrors();

        assertEquals("Movie list should be empty before change events are fired.",
                0, moviesViewModel.getMovies().size());

        // Notify the view model for the change.
        observerCaptor.getValue().onUpdate(movieCollection1, Action.AddItemToFront, movie1, null);

        // Verify the list after change events are notified.
        verifyList1(moviesViewModel.getMovies());
    }

    // endregion

    // region Private Methods

    private void verifyList1(final ObservableList<MovieViewModel> movieViewModelList) {
        assertFalse("Movie list is empty.", movieViewModelList.isEmpty());
        assertEquals("Movie list size is not one.", movieViewModelList.size(), 1);
        final MovieViewModel movieViewModel = movieViewModelList.get(0);
        verifyMovie1ViewModel(movieViewModel);
    }

    private void verifyMovie1ViewModel(final MovieViewModel movieViewModel) {
        assertEquals("Movie title does not match.", movieViewModel.getTitle(), MOVIE1_TITLE);
        assertEquals("Movie poster URL does not match.",
                movieViewModel.getPosterUrl(100), MOVIE1_POSTER_URL);
    }

    private void verifyMovie1DetailsViewModel(final MovieDetailsViewModel movieDetailsViewModel) {
        assertEquals("Movie title does not match.", movieDetailsViewModel.getTitle(), MOVIE1_TITLE);
        assertEquals("Movie poster URL does not match.",
                movieDetailsViewModel.getPosterUrl(100), MOVIE1_POSTER_URL);
        assertNotNull("Similar movie list should not be null.", movieDetailsViewModel.getSimilarMovies());
    }

    // endregion
}
