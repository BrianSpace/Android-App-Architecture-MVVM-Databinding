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

package com.github.brianspace.moviebrowser.repository

import com.github.brianspace.moviebrowser.repository.data.MovieData
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Interface for favorite movie storage.
 */
interface IFavoriteStore {

    /**
     * Get all favorite movies.
     *
     * @return RxJava `Single` for the list of movies.
     */
    val allFavoriteMovies: Single<List<MovieData>>

    /**
     * Clear all favorite movies.
     *
     * @return true if the data is cleared (false if failed to clear).
     */
    fun clearData(): Boolean

    /**
     * Add a favorite movie.
     *
     * @param movie the data layer movie object.
     * @return RxJava `Single` for the boolean result (false if failed to add).
     */
    fun addFavoriteMovie(movie: MovieData): Single<Boolean>

    /**
     * Get a favorite movie by ID.
     *
     * @param favoriteId ID of the movie.
     * @return RxJava `Maybe` for the movie.
     */
    fun getFavoriteMovie(favoriteId: Long): Maybe<MovieData>

    /**
     * Delete a favorite movie.
     *
     * @param movie the data layer movie object.
     * @return RxJava `Single` for the boolean result (false if failed to delete).
     */
    fun deleteFavoriteMovie(movie: MovieData): Single<Boolean>
}
