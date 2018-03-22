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

import com.github.brianspace.moviebrowser.repository.data.MovieData

/**
 * Interface for model layer entities store.
 * The store is used to access model layer objects and it ensures the uniqueness of model entities.
 */
interface IEntityStore {

    /**
     * Find or create movie model from the data layer movie object.
     * @param movieData data layer movie object
     * @return model matching the data layer movie object.
     */
    fun getMovieModel(movieData: MovieData): Movie

    /**
     * Find movie model object by ID.
     * @param id the movie ID
     * @return the movie model matching the ID, or null if not found.
     */
    fun findMovieById(id: Int): Movie?
}