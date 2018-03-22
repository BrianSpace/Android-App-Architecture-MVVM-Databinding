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

import com.github.brianspace.common.objstore.IEntity
import com.github.brianspace.common.observable.ObjectObservableBase
import com.github.brianspace.moviebrowser.BuildConfig
import com.github.brianspace.moviebrowser.repository.IMovieDbService
import com.github.brianspace.moviebrowser.repository.data.MovieData
import com.github.brianspace.moviebrowser.repository.data.MovieDetailsData
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope
import io.reactivex.Completable
import io.reactivex.Single
import java.security.InvalidParameterException

/**
 * Model of a movie.
 */
class Movie internal constructor(
    /**
     * Interface for accessing TMDb Web API.
     */
    private val movieDbService: IMovieDbService,
    /**
     * Interface for model entity store.
     */
    entityStore: IEntityStore,
    /**
     * Data layer movie object.
     */
    internal val movieData: MovieData
) : ObjectObservableBase(), IEntity {

    // region Private Properties

    /**
     * Similar movie list.
     */
    private val similarMovies: SimilarMovies

    /**
     * Flag indicating the status of loading details.
     */
    private var isLoadingDetails: Boolean = false

    /**
     * Result returned by loadDetails().
     */
    private var detailsCompletable: Completable? = null

    // endregion

    // region Public Properties

    /**
     * The relative path (in URL) of the movie poster, without leading backslash.
     */
    val posterPath: String?

    /**
     * The relative path (in URL) of the movie backdrop, without leading backslash.
     */
    val backdropPath: String?

    /**
     * Movie details.
     */
    var details: Details? = null
        private set

    /**
     * Whether the movie is favorite (true) or not (false).
     */
    var isFavorite: Boolean = false
        internal set(value) {
            if (field != value) {
                field = value
                setChanged()
                notifyObservers()
            }
        }

    /**
     * Movie ID.
     */
    override val id: Int
        get() = movieData.id

    /**
     * Movie title.
     */
    val title: String
        get() = movieData.title!!

    /**
     * Movie overview.
     */
    val overview: String?
        get() = movieData.overview

    /**
     * Average vote score (0 ~ 10).
     */
    val voteAverage: Float
        get() = movieData.voteAverage

    // endregion

    // region Private Types

    /**
     * Similar movie list type.
     */
    private inner class SimilarMovies internal constructor(
        movieDbService: IMovieDbService, entityStore: IEntityStore
    ) : MovieCollection(movieDbService, entityStore) {

        override val firstPage: Single<PagingEnvelope<MovieData>>
            get() = movieDbService.getSimilarMovies(movieData.id, null)

        override fun getNextPage(prev: PagingEnvelope<MovieData>?): Single<PagingEnvelope<MovieData>> {
            return movieDbService.getSimilarMovies(movieData.id, prev)
        }
    }

    // endregion

    // region Public Types

    /**
     * Movie details model.
     */
    inner class Details internal constructor(private val detailsData: MovieDetailsData) {
        /**
         * Get movie tagline.
         * @return Tagline text.
         */
        val tagline: String?
            get() = detailsData.tagline
    }

    init {
        if (BuildConfig.DEBUG && entityStore.findMovieById(movieData.id) != null) {
            throw InvalidParameterException(
                "DO NOT create a different instance for the same ID!"
            )
        }

        posterPath = getValidImagePath(movieData.posterPath)
        backdropPath = getValidImagePath(movieData.backdropPath)
        similarMovies = SimilarMovies(movieDbService, entityStore)
    }

    // endregion

    // region Public Methods

    /**
     * Get a list of similar movies.
     * @return similar movies list
     */
    fun getSimilarMovies(): IMovieCollection {
        return similarMovies
    }

    /**
     * Load movie details. The result of getDetails() may change.
     *
     * @return RxJava `Completable`
     */
    fun loadDetails(): Completable {
        if (isLoadingDetails && detailsCompletable != null) {
            return detailsCompletable!!
        }

        isLoadingDetails = true
        detailsCompletable = movieDbService.getMovieDetails(movieData.id)
            .map { result ->
                details = Details(result)
                setChanged()
                notifyObservers()
                Irrelevant.INSTANCE
            }
            .toCompletable()
            .doFinally { isLoadingDetails = false }
        return detailsCompletable!!
    }

    // endregion

    // region Private Methods

    private fun getValidImagePath(path: String?): String? {
        return if (path != null && !path.isEmpty()) {
            if (path.startsWith(Constants.BACK_SLASH)) {
                path.substring(1)
            } else path

        } else null

    }

    // endregion
}
