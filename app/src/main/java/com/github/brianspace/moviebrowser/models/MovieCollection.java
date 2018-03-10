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

import android.support.annotation.NonNull;
import android.util.Log;
import com.github.brianspace.common.observable.CollectionObservableBase;
import com.github.brianspace.common.observable.ICollectionObserver.Action;
import com.github.brianspace.moviebrowser.BuildConfig;
import com.github.brianspace.moviebrowser.repository.IMovieDbService;
import com.github.brianspace.moviebrowser.repository.data.MovieData;
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for movie list.
 */
public abstract class MovieCollection extends CollectionObservableBase implements IMovieCollection {
    // region Private Constants

    /**
     * Tag for logging.
     */
    private static final String LOG_TAG = MovieCollection.class.getSimpleName();

    // endregion

    // region Protected Fields

    /**
     * Interface for accessing TMDb Web API.
     */
    protected final IMovieDbService movieDbService;
    /**
     * Interface for model entity store.
     */
    protected final IEntityStore entityStore;

    /**
     * The data layer list of the pages of movie data.
     */
    protected final List<PagingEnvelope<MovieData>> resultList = new ArrayList<>();
    /**
     * List of model layer movie objects.
     */
    protected final List<Movie> movies = new ArrayList<>();
    /**
     * State of data loading.
     */
    protected boolean isLoading;

    // endregion

    // region Private Fields

    /**
     * Result returned by load().
     */
    private Completable loadCompletable;

    /**
     * Result returned by refresh().
     */
    private Completable refreshCompletable;

    /**
     * Result returned by loadNextPage().
     */
    private Completable nextPageCompletable;

    /**
     * RxJava mapping function to extract the data layer pages of movies into model layer movie list.
     */
    private final Function<PagingEnvelope<MovieData>, Object> resultHandler =
            new Function<PagingEnvelope<MovieData>, Object>() {
                @Override
                public Object apply(final PagingEnvelope<MovieData> moviesResult) {
                    if (moviesResult.getResults().isEmpty()) {
                        return Irrelevant.INSTANCE;
                    }

                    onMoviePage(moviesResult);

                    isLoading = false;
                    return Irrelevant.INSTANCE;
                }

                private void onMoviePage(final PagingEnvelope<MovieData> moviesResult) {
                    resultList.add(moviesResult);
                    final List<Object> appendList = new ArrayList<>();
                    for (final MovieData movie : moviesResult.getResults()) {
                        // Validate
                        if (movie.isValid()) {
                            // De-duplicate.
                            final Movie model = entityStore.findMovieById(movie.getId());
                            if (model == null || movies.indexOf(model) < 0) {
                                final Movie movieModel = entityStore.getMovieModel(movie);
                                movies.add(0, movieModel);
                                appendList.add(0, movieModel);
                            }
                        } else if (BuildConfig.DEBUG) {
                            Log.w(LOG_TAG, "Invalid movie data for: \n" + movie.toString());
                        }
                    }

                    if (!appendList.isEmpty()) {
                        setChanged();
                        notifyObservers(Action.AppendRange, null, appendList);
                    }
                }
            };

    // endregion

    // region Constructors

    /**
     * Constructor for model layer internal usage.
     *
     * @param movieDbService interface for accessing TMDb Web API.
     * @param entityStore interface for model entity store.
     */
    protected MovieCollection(@NonNull final IMovieDbService movieDbService,
            @NonNull final IEntityStore entityStore) {
        this.movieDbService = movieDbService;
        this.entityStore = entityStore;
    }

    // endregion

    // region Public Overrides

    @Override
    public boolean isLoaded() {
        return !movies.isEmpty();
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @NonNull
    @Override
    public List<Movie> getMovies() {
        return movies;
    }

    @NonNull
    @Override
    public Completable load() {
        if (isLoading && loadCompletable != null) {
            return loadCompletable;
        }

        if (isLoaded()) {
            return Completable.complete();
        }

        isLoading = true;
        loadCompletable = getFirstPage()
                .doFinally(() -> isLoading = false)
                .map(resultHandler)
                .toCompletable();
        return loadCompletable;
    }

    @NonNull
    @Override
    public Completable refresh() {
        if (isLoading && refreshCompletable != null) {
            return refreshCompletable;
        }

        isLoading = true;
        refreshCompletable = getFirstPage()
                .doFinally(() -> isLoading = false)
                .map(moviesResult -> {
                    resultList.clear();
                    movies.clear();
                    setChanged();
                    notifyObservers(Action.Clear, null, null);
                    return moviesResult;
                })
                .map(resultHandler)
                .toCompletable();
        return refreshCompletable;
    }

    @Override
    public boolean hasNexPage() {
        final int resultSize = resultList.size();
        if (resultSize == 0) {
            return true;
        }

        final PagingEnvelope<MovieData> prevResult = resultList.get(resultSize - 1);
        return prevResult.getPage() < prevResult.getTotalPages();
    }

    @NonNull
    @Override
    public Completable loadNextPage() {
        if (isLoading && nextPageCompletable != null) {
            return nextPageCompletable;
        }

        final int resultSize = resultList.size();
        final PagingEnvelope<MovieData> prevResult = resultSize > 0 ? resultList.get(resultSize - 1) : null;
        isLoading = true;
        nextPageCompletable = getNextPage(prevResult)
                .subscribeOn(Schedulers.io())
                .doFinally(() -> isLoading = false)
                .map(resultHandler)
                .toCompletable();
        return nextPageCompletable;
    }

    // endregion

    // region Protected Methods

    /**
     * Get the first page of movie list.
     * Subclass should implement to return the first page of movies.
     *
     * @return RxJava Observable of a page of movie data list.
     */
    protected abstract Single<PagingEnvelope<MovieData>> getFirstPage();

    /**
     * Get the next page of movie list.
     * Subclass should implement to return the next page of movies based on the previous page.
     *
     * @return RxJava Observable of a page of movie data list.
     */
    protected abstract Single<PagingEnvelope<MovieData>> getNextPage(PagingEnvelope<MovieData> prev);

    // endregion
}
