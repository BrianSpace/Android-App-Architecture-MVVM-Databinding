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

import android.util.Log
import com.github.brianspace.moviebrowser.repository.IMovieDbService
import com.github.brianspace.moviebrowser.repository.data.Configuration
import com.github.brianspace.moviebrowser.repository.data.MovieData
import com.github.brianspace.moviebrowser.repository.data.MovieDetailsData
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.OkHttpClient

// region Private Constants

/**
 * Tag for logcat.
 */
private const val TAG = "MovieDbService"

// endregion

/**
 * Accessing TMDb Web API with [IMovieDbApi].
 */
@Singleton
internal class MovieDbService
@Inject
constructor(
    /**
     * OkHttpClient for requesting TMDb Web API. Used to clear cache.
     */
    private val okHttpClient: OkHttpClient,
    /**
     * IMovieDbApi instance.
     */
    private val movieDbApi: IMovieDbApi
) : IMovieDbService {

    // region Private Properties

    /**
     * Default empty result for paged movie list.
     */
    private val emptyResult = PagingEnvelope<MovieData>(0, 0, 0, null)

    // endregion

    // region Public Overrides

    override val configuration: Single<Configuration>
        get() = movieDbApi.configuration

    override fun clearCache(): Boolean {
        try {
            okHttpClient.cache().evictAll()
            return true
        } catch (e: IOException) {
            Log.e(TAG, "clearCache failed: " + e.toString())
        }

        return false
    }

    override fun getMovieNowPlaying(
        previous: PagingEnvelope<MovieData>?
    ): Single<PagingEnvelope<MovieData>> {
        if (previous == null) {
            return movieDbApi.getMovieNowPlaying(1)
        }

        val prevPage = previous.page
        return if (prevPage < previous.totalPages) {
            movieDbApi.getMovieNowPlaying(prevPage + 1).subscribeOn(Schedulers.io())
        } else Single.just(emptyResult)

    }

    override fun getMovieDetails(id: Int): Single<MovieDetailsData> {
        return movieDbApi.getMovieDetails(id)
    }

    override fun getSimilarMovies(
        id: Int,
        previous: PagingEnvelope<MovieData>?
    ): Single<PagingEnvelope<MovieData>> {
        if (previous == null) {
            return movieDbApi.getSimilarMovies(id, 1)
        }

        val prevPage = previous.page
        return if (prevPage < previous.totalPages) {
            movieDbApi.getSimilarMovies(id, prevPage + 1)
        } else Single.just(emptyResult)
    }
}
