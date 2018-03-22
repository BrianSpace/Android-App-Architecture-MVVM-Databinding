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

import android.util.Log
import com.github.brianspace.common.observable.CollectionObservableBase
import com.github.brianspace.common.observable.ICollectionObserver.Action
import com.github.brianspace.moviebrowser.BuildConfig
import com.github.brianspace.moviebrowser.repository.IMovieDbService
import com.github.brianspace.moviebrowser.repository.data.MovieData
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

// region Private Constants

/**
 * Tag for logging.
 */
private val LOG_TAG = MovieCollection::class.java.simpleName

// endregion

/**
 * Base class for movie list.
 */
abstract class MovieCollection
protected constructor(
    /**
     * Interface for accessing TMDb Web API.
     */
    protected val movieDbService: IMovieDbService,
    /**
     * Interface for model entity store.
     */
    protected val entityStore: IEntityStore
) : CollectionObservableBase(), IMovieCollection {

    // region Protected Properties

    /**
     * The data layer list of the pages of movie data.
     */
    protected val resultList: MutableList<PagingEnvelope<MovieData>> = ArrayList()

    /**
     * List of model layer movie objects.
     */
    protected val movieList: MutableList<Movie> = ArrayList()

    // endregion

    // region Private Properties

    /**
     * Result returned by load().
     */
    private var loadCompletable: Completable? = null

    /**
     * Result returned by refresh().
     */
    private var refreshCompletable: Completable? = null

    /**
     * Result returned by loadNextPage().
     */
    private var nextPageCompletable: Completable? = null

    /**
     * RxJava mapping function to extract the data layer pages of movies into model layer movie list.
     */
    private val resultHandler = object : Function<PagingEnvelope<MovieData>, Any> {
        override fun apply(moviesResult: PagingEnvelope<MovieData>): Any {
            if (moviesResult.results!!.isEmpty()) {
                return Irrelevant.INSTANCE
            }

            onMoviePage(moviesResult)

            isLoading = false
            return Irrelevant.INSTANCE
        }

        private fun onMoviePage(moviesResult: PagingEnvelope<MovieData>) {
            resultList.add(moviesResult)
            val appendList = ArrayList<Any>()
            for (movie in moviesResult.results!!) {
                // Validate
                if (movie.isValid) {
                    // De-duplicate.
                    val model = entityStore.findMovieById(movie.id)
                    if (model == null || movieList.indexOf(model) < 0) {
                        val movieModel = entityStore.getMovieModel(movie)
                        movieList.add(movieModel)
                        appendList.add(movieModel)
                    }
                } else if (BuildConfig.DEBUG) {
                    Log.w(LOG_TAG, "Invalid movie data for: \n" + movie.toString())
                }
            }

            if (!appendList.isEmpty()) {
                setChanged()
                notifyObservers(Action.AppendRange, null, appendList)
            }
        }
    }

    // endregion

    // region Public Overrides

    override var isLoading: Boolean = false
        protected set(value) {
            field = value
        }

    override val isLoaded: Boolean
        get() = !movieList.isEmpty()

    override val movies: List<Movie>
        get() = movieList

    override fun load(): Completable {
        if (isLoading && loadCompletable != null) {
            return loadCompletable!!
        }

        if (isLoaded) {
            return Completable.complete()
        }

        isLoading = true
        loadCompletable = firstPage
            .doFinally { isLoading = false }
            .map(resultHandler)
            .toCompletable()
        return loadCompletable!!
    }

    override fun refresh(): Completable {
        if (isLoading && refreshCompletable != null) {
            return refreshCompletable!!
        }

        isLoading = true
        refreshCompletable = firstPage
            .doFinally { isLoading = false }
            .map { moviesResult ->
                resultList.clear()
                movieList.clear()
                setChanged()
                notifyObservers(Action.Clear, null, null)
                moviesResult
            }
            .map(resultHandler)
            .toCompletable()
        return refreshCompletable!!
    }

    override fun hasNexPage(): Boolean {
        val resultSize = resultList.size
        if (resultSize == 0) {
            return true
        }

        val prevResult = resultList[resultSize - 1]
        return prevResult.page < prevResult.totalPages
    }

    override fun loadNextPage(): Completable {
        if (isLoading && nextPageCompletable != null) {
            return nextPageCompletable!!
        }

        val resultSize = resultList.size
        val prevResult = if (resultSize > 0) resultList[resultSize - 1] else null
        isLoading = true
        nextPageCompletable = getNextPage(prevResult)
            .subscribeOn(Schedulers.io())
            .doFinally { isLoading = false }
            .map(resultHandler)
            .toCompletable()
        return nextPageCompletable!!
    }

    // endregion

    // region Protected Methods

    /**
     * Get the first page of movie list.
     * Subclass should implement to return the first page of movies.
     *
     * @return RxJava Observable of a page of movie data list.
     */
    protected abstract val firstPage: Single<PagingEnvelope<MovieData>>

    /**
     * Get the next page of movie list.
     * Subclass should implement to return the next page of movies based on the previous page.
     *
     * @return RxJava Observable of a page of movie data list.
     */
    protected abstract fun getNextPage(prev: PagingEnvelope<MovieData>?): Single<PagingEnvelope<MovieData>>

    // endregion
}
