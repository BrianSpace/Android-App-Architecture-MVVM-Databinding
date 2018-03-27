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

import com.github.brianspace.moviebrowser.models.IEntityStore
import com.github.brianspace.moviebrowser.models.IFavoriteMovieCollection
import com.github.brianspace.moviebrowser.models.IImageConfig
import com.github.brianspace.moviebrowser.models.Movie
import javax.inject.Inject
import javax.inject.Singleton

/**
 * View model repository.
 */
@Singleton
internal class ViewModelFactory
@Inject
constructor(
    /**
     * Interface for image configuration.
     */
    private val imageConfig: IImageConfig,
    /**
     * Interface for model entity store.
     */
    private val entityStore: IEntityStore,
    /**
     * Model layer collection of favorite movies.
     */
    private val favoriteMovieCollection: IFavoriteMovieCollection
) : IViewModelFactory {

    // region Public Overrides

    override fun createMovieViewModel(movie: Movie): MovieViewModel {
        return MovieViewModel(movie, imageConfig, favoriteMovieCollection)
    }

    override fun createMovieViewModelById(id: Int): MovieViewModel? {
        val movie = entityStore.findMovieById(id)
        return if (movie == null) null else createMovieViewModel(movie)
    }

    override fun createMovieDetailsViewModelById(id: Int): MovieDetailsViewModel? {
        val movie = entityStore.findMovieById(id)
        return if (movie == null) null else MovieDetailsViewModel(movie, this, imageConfig, favoriteMovieCollection)
    }

    // endregion
}
