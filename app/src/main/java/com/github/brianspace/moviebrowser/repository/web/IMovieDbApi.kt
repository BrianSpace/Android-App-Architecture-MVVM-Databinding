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

package com.github.brianspace.moviebrowser.repository.web

import com.github.brianspace.moviebrowser.repository.data.Configuration
import com.github.brianspace.moviebrowser.repository.data.MovieData
import com.github.brianspace.moviebrowser.repository.data.MovieDetailsData
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The Movie DB (TMDb) Web API interface (for Retrofit).
 * @see [The Movie Database API](https://developers.themoviedb.org/3)
 */
internal interface IMovieDbApi {
    /**
     * Get configuration (image base URL, image size, etc.).
     *
     * @return RxJava `Single` for the configuration.
     */
    @get:GET("configuration")
    val configuration: Single<Configuration>

    /**
     * Get the list of now playing movies.
     *
     * @param page the page number.
     * @return RxJava `Single` for the page of movie list.
     */
    @GET("movie/now_playing")
    fun getMovieNowPlaying(@Query("page") page: Int): Single<PagingEnvelope<MovieData>>

    /**
     * Get extra details for a movie.
     *
     * @param id the ID of the movie to request for details.
     * @return  RxJava `Single` for the details data.
     */
    @GET("movie/{id}")
    fun getMovieDetails(@Path("id") id: Int): Single<MovieDetailsData>

    /**
     * Get the list of similar movies.
     *
     * @param id the ID of the movie to request for similar movies.
     * @param page the page number.
     * @return RxJava `Single` for the page of movie list.
     */
    @GET("movie/{id}/similar")
    fun getSimilarMovies(@Path("id") id: Int, @Query("page") page: Int): Single<PagingEnvelope<MovieData>>
}
