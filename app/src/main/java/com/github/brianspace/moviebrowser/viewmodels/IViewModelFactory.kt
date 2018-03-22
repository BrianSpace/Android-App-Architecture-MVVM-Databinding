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

import com.github.brianspace.moviebrowser.models.Movie

/**
 * Interface for view model object store.
 */
interface IViewModelFactory {

    /**
     * Create a new movie view model from the supplied model.
     * @param movie Model layer movie object.
     * @return the corresponding movie view model.
     */
    fun createMovieViewModel(movie: Movie): MovieViewModel

    /**
     * Get a movie view model by the ID.
     * @param id ID of the movie to request for.
     * @return the corresponding movie view model, or null if not found.
     */
    fun createMovieViewModelById(id: Int): MovieViewModel?

    /**
     * Get a movie details view model by the ID.
     * @param id ID of the movie to request for.
     * @return the corresponding movie details view model, or null if not found.
     */
    fun createMovieDetailsViewModelById(id: Int): MovieDetailsViewModel?
}
