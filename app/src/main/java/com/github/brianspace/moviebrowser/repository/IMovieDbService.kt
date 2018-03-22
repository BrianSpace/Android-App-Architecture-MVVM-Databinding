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

import com.github.brianspace.moviebrowser.repository.data.Configuration
import com.github.brianspace.moviebrowser.repository.data.MovieData
import com.github.brianspace.moviebrowser.repository.data.MovieDetailsData
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope
import io.reactivex.Single

/**
 * Interface for accessing TMDb Web API.
 */
interface IMovieDbService {

    /**
     * Get configuration (image base URL, image size, etc.).
     *
     * @return RxJava `Single` for the configuration.
     */
    val configuration: Single<Configuration>

    /**
     * Clear the HTTP cache.
     * @return true for success and false for failure.
     */
    fun clearCache(): Boolean

    /**
     * Get the list of now playing movies.
     *
     * @param previous the previous page of movie data list.
     * @return RxJava `Single` for the page of movie list.
     */
    fun getMovieNowPlaying(previous: PagingEnvelope<MovieData>?): Single<PagingEnvelope<MovieData>>

    /**
     * Get extra details for a movie.
     *
     * @param id the ID of the movie to request for details.
     * @return  RxJava `Single` for the details data.
     */
    fun getMovieDetails(id: Int): Single<MovieDetailsData>

    /**
     * Get the list of similar movies.
     *
     * @param id the ID of the movie to request for similar movies.
     * @param previous  the previous page of movie data list.
     * @return  RxJava `Single` for the page of movie list.
     */
    fun getSimilarMovies(id: Int, previous: PagingEnvelope<MovieData>?): Single<PagingEnvelope<MovieData>>
}
