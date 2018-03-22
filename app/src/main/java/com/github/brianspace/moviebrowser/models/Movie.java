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
import android.support.annotation.Nullable;
import com.github.brianspace.common.objstore.IEntity;
import com.github.brianspace.common.observable.ObjectObservableBase;
import com.github.brianspace.moviebrowser.BuildConfig;
import com.github.brianspace.moviebrowser.repository.IMovieDbService;
import com.github.brianspace.moviebrowser.repository.data.MovieData;
import com.github.brianspace.moviebrowser.repository.data.MovieDetailsData;
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.security.InvalidParameterException;

/**
 * Model of a movie.
 */
public class Movie extends ObjectObservableBase implements IEntity {

    // region Private Fields

    /**
     * Interface for accessing TMDb Web API.
     */
    private final IMovieDbService movieDbService;
    /**
     * Data layer movie object.
     */
    private final MovieData movieData;

    /**
     * Poster image path.
     */
    private final String posterPath;

    /**
     * Backdrop image path.
     */
    private final String backdropPath;

    /**
     * Flag indicating the status of loading details.
     */
    private boolean isLoadingDetails;

    /**
     * Movie details.
     */
    private Details details;

    /**
     * Result returned by loadDetails().
     */
    private Completable detailsCompletable;

    /**
     * Whether the movie is favorite or not.
     */
    private boolean isFavorite;

    /**
     * Similar movie list.
     */
    private final SimilarMovies similarMovies;

    // endregion

    // region Private Types

    /**
     * Similar movie list type.
     */
    private class SimilarMovies extends MovieCollection {

        /* default */ SimilarMovies(
                @NonNull final IMovieDbService movieDbService,
                @NonNull final IEntityStore entityStore) {
            super(movieDbService, entityStore);
        }

        @Override
        protected Single<PagingEnvelope<MovieData>> getFirstPage() {
            return movieDbService.getSimilarMovies(movieData.getId(), null);
        }

        @Override
        protected Single<PagingEnvelope<MovieData>> getNextPage(final PagingEnvelope<MovieData> prev) {
            return movieDbService.getSimilarMovies(movieData.getId(), prev);
        }
    }

    // endregion

    // region Public Types

    /**
     * Movie details model.
     */
    public class Details {

        /**
         * Movie details data layer object.
         */
        private final MovieDetailsData detailsData;

        /* default */ Details(@NonNull final MovieDetailsData details) {
            detailsData = details;
        }

        /**
         * Get movie tagline.
         * @return Tagline text.
         */
        @Nullable
        public String getTagline() {
            return detailsData.getTagline();
        }
    }

    // endregion

    // region Constructors

    /* default */ Movie(@NonNull final IMovieDbService movieDbService, @NonNull final IEntityStore entityStore,
            @NonNull final MovieData movie) {
        if (BuildConfig.DEBUG && entityStore.findMovieById(movie.getId()) != null) {
            throw new InvalidParameterException(
                    "DO NOT create a different instance for the same ID!");
        }

        this.movieDbService = movieDbService;
        this.movieData = movie;

        posterPath = getValidImagePath(movie.getPosterPath());
        backdropPath = getValidImagePath(movie.getBackdropPath());

        this.similarMovies = new SimilarMovies(movieDbService, entityStore);
    }

    // endregion

    // region Public Methods

    /**
     * Get movie ID.
     */
    public int getId() {
        return movieData.getId();
    }

    /**
     * Get movie title.
     * @return title
     */
    @NonNull
    public String getTitle() {
        return movieData.getTitle();
    }

    /**
     * Get movie overview.
     * @return overview
     */
    @Nullable
    public String getOverview() {
        return movieData.getOverview();
    }

    /**
     * Get the relative path (in URL) of the movie backdrop, without leading backslash.
     * @return path of the movie backdrop
     */
    @Nullable
    public String getBackdropPath() {
        return backdropPath;
    }

    /**
     * Get the relative path (in URL) of the movie poster, without leading backslash.
     * @return path of the movie poster
     */
    @NonNull
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * Get average vote score (0 ~ 10).
     * @return average vote score
     */
    public float getVoteAverage() {
        return movieData.getVoteAverage();
    }

    /**
     * Get extra movie details.
     * @return details
     */
    @Nullable
    public Details getDetails() {
        return details;
    }

    /**
     * Get a list of similar movies.
     * @return similar movies list
     */
    @NonNull
    public IMovieCollection getSimilarMovies() {
        return similarMovies;
    }

    /**
     * Load movie details. The result of getDetails() may change.
     *
     * @return RxJava {@code Completable}
     */
    @NonNull
    public Completable loadDetails() {
        if (isLoadingDetails && detailsCompletable != null) {
            return detailsCompletable;
        }

        isLoadingDetails = true;
        detailsCompletable = movieDbService.getMovieDetails(movieData.getId())
                .map(result -> {
                    details = new Details(result);
                    setChanged();
                    notifyObservers();
                    return Irrelevant.INSTANCE;
                })
                .toCompletable()
                .doFinally(() -> isLoadingDetails = false);
        return detailsCompletable;
    }

    /**
     * Whether the movie is a favorite or not.
     * @return true if the movie is a favorite, otherwise false.
     */
    public boolean isFavorite() {
        return isFavorite;
    }

    // endregion

    // region Package Private Methods

    /**
     * Get data layer object.
     * @return movie data
     */
    @NonNull
    /* default */ MovieData getMovieData() {
        return movieData;
    }

    /**
     * Set favorite state.
     * @param value true if is a favorite movie, otherwise false.
     */
    /* default */ void setFavorite(final boolean value) {
        if (isFavorite != value) {
            isFavorite = value;
            setChanged();
            notifyObservers();
        }
    }

    // endregion

    // region Private Methods

    @Nullable
    private String getValidImagePath(final String path) {
        if (path != null && !path.isEmpty()) {
            if (path.startsWith(Constants.BACK_SLASH)) {
                return path.substring(1);
            }

            return path;
        }

        return null;
    }

    // endregion
}
