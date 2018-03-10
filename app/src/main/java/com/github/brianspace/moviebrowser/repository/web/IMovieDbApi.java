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

package com.github.brianspace.moviebrowser.repository.web;

import com.github.brianspace.moviebrowser.repository.data.Configuration;
import com.github.brianspace.moviebrowser.repository.data.MovieData;
import com.github.brianspace.moviebrowser.repository.data.MovieDetailsData;
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The Movie DB (TMDb) Web API interface (for Retrofit).
 * @see <a href="https://developers.themoviedb.org/3">The Movie Database API</a>
 */
interface IMovieDbApi {
    /**
     * Get configuration (image base URL, image size, etc.).
     *
     * @return RxJava {@code Single} for the configuration.
     */
    @GET("configuration")
    Single<Configuration> getConfiguration();

    /**
     * Get the list of now playing movies.
     *
     * @param page the page number.
     * @return RxJava {@code Single} for the page of movie list.
     */
    @GET("movie/now_playing")
    Single<PagingEnvelope<MovieData>> getMovieNowPlaying(@Query("page") int page);

    /**
     * Get extra details for a movie.
     *
     * @param id the ID of the movie to request for details.
     * @return  RxJava {@code Single} for the details data.
     */
    @GET("movie/{id}")
    Single<MovieDetailsData> getMovieDetails(@Path("id") int id);

    /**
     * Get the list of similar movies.
     *
     * @param id the ID of the movie to request for similar movies.
     * @param page the page number.
     * @return RxJava {@code Single} for the page of movie list.
     */
    @GET("movie/{id}/similar")
    Single<PagingEnvelope<MovieData>> getSimilarMovies(@Path("id") int id, @Query("page") int page);
}
