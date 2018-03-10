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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.github.brianspace.moviebrowser.models.IEntityStore;
import com.github.brianspace.moviebrowser.models.IFavoriteMovieCollection;
import com.github.brianspace.moviebrowser.models.IImageConfig;
import com.github.brianspace.moviebrowser.models.Movie;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * View model repository.
 */
@Singleton
class ViewModelFactory implements IViewModelFactory {

    // region Protected/Private Fields

    /**
     * Interface for image configuration.
     */
    protected final IImageConfig imageConfig;

    /**
     * Model layer collection of favorite movies.
     */
    protected final IFavoriteMovieCollection favoriteMovieCollection;

    /**
     * Interface for model entity store.
     */
    private final IEntityStore entityStore;

    // endregion

    // region Constructors

    /**
     * Constructor.
     *
     * @param imageConfig             interface for image configuration.
     * @param favoriteMovieCollection model layer collection of favorite movies.
     */
    @Inject
    /* default */ ViewModelFactory(@NonNull final IImageConfig imageConfig,
            @NonNull final IEntityStore entityStore,
            @NonNull final IFavoriteMovieCollection favoriteMovieCollection) {
        this.imageConfig = imageConfig;
        this.entityStore = entityStore;
        this.favoriteMovieCollection = favoriteMovieCollection;
    }

    // endregion

    // region Public Overrides

    @Override
    @NonNull
    public MovieViewModel createMovieViewModel(@NonNull final Movie movie) {
        return new MovieViewModel(movie, imageConfig, favoriteMovieCollection);
    }

    @Override
    @Nullable
    public MovieViewModel createMovieViewModelById(final int id) {
        final Movie movie = entityStore.findMovieById(id);
        return movie == null ? null : createMovieViewModel(movie);
    }

    @Nullable
    @Override
    public MovieDetailsViewModel createMovieDetailsViewModelById(final int id) {
        final Movie movie = entityStore.findMovieById(id);
        return movie == null ? null : new MovieDetailsViewModel(movie, this, imageConfig, favoriteMovieCollection);
    }

    // endregion
}
