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
import com.github.brianspace.common.observable.CollectionObservableBase;
import com.github.brianspace.common.observable.ICollectionObserver.Action;
import com.github.brianspace.moviebrowser.repository.IFavoriteStore;
import com.github.brianspace.moviebrowser.repository.data.MovieData;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * The list of favorite movies.
 */
@Singleton
class FavoriteMovieCollection extends CollectionObservableBase implements IFavoriteMovieCollection {
    // region Private Fields

    /**
     * Interface for favorite database.
     */
    private final IFavoriteStore favoriteStore;
    /**
     * Interface for model entity store.
     */
    private final IEntityStore entityStore;

    // endregion

    // region Protected Fields

    /**
     * The list of favorite movies.
     */
    protected final List<Movie> movies = new ArrayList<>();
    /**
     * State of loading data from database.
     */
    protected boolean isLoading;

    // endregion

    // region Constructors

    /**
     * Create a new instance of FavoriteMovieCollection.
     *
     * @param favoriteStore interface for accessing favorite database.
     * @param entityStore interface for model entity store.
     */
    @Inject
    protected FavoriteMovieCollection(@NonNull final IFavoriteStore favoriteStore,
            @NonNull final IEntityStore entityStore) {
        this.favoriteStore = favoriteStore;
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
        isLoading = true;
        return favoriteStore.getAllFavoriteMovies()
                .map(movieList -> {
                    for (final MovieData movie : movieList) {
                        final Movie movieModel = entityStore.getMovieModel(movie);
                        movieModel.setFavorite(true);
                        movies.add(movieModel);
                        setChanged();
                        notifyObservers(Action.AppendItem, movieModel, null);
                    }

                    return Irrelevant.INSTANCE;
                })
                .toCompletable()
                .doFinally(() -> {
                    isLoading = false;
                });
    }

    @NonNull
    @Override
    public Completable refresh() {
        movies.clear();
        setChanged();
        notifyObservers(Action.Clear, null, null);
        return load();
    }

    @Override
    public boolean hasNexPage() {
        return false;
    }

    @NonNull
    @Override
    public Completable loadNextPage() {
        return Completable.complete();
    }

    @NonNull
    @Override
    public Single<Boolean> addToFavorite(@NonNull final Movie movie) {
        final Movie found = findModelWithSameId(movie);
        if (found != null || movie.isFavorite()) {
            throw new InvalidParameterException("The movie is already in favorite list!");
        }

        return favoriteStore.addFavoriteMovie(movie.getMovieData())
                .map(result -> {
                    if (result) {
                        movies.add(0, movie);
                        movie.setFavorite(true);
                        setChanged();
                        notifyObservers(Action.AddItemToFront, movie, null);
                    }
                    return result;
                });
    }

    @NonNull
    @Override
    public Single<Boolean> removeFromFavorite(@NonNull final Movie movie) {
        final Movie found = findModelWithSameId(movie);

        if (found != null && movie.isFavorite()) {
            return favoriteStore.deleteFavoriteMovie(movie.getMovieData()).map(result -> {
                if (result) {
                    movies.remove(found);
                    movie.setFavorite(false);
                    setChanged();
                    notifyObservers(Action.RemoveItem, movie, null);
                }

                return result;
            });
        }

        throw new InvalidParameterException("The movie is not a favorite movie!");
    }

    // endregion

    // region Package Private Methods

    /**
     * Clear all favorite movies. Used by {@link DataCleaner}.
     * For simplicity, database is not cleared here but in the cleaner.
     */
    /* default */ void clear() {
        for (final Movie item : movies) {
            item.setFavorite(false);
        }

        movies.clear();
        setChanged();
        notifyObservers(Action.Clear, null, null);
    }

    // endregion

    // region Private Methods

    @Nullable
    private Movie findModelWithSameId(@NonNull final Movie movie) {
        final int id = movie.getId();
        Movie found = null;
        for (final Movie item : movies) {
            if (item.getId() == id) {
                found = item;
                break;
            }
        }

        return found;
    }

    // endregion
}