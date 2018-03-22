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

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import com.github.brianspace.common.observable.ICollectionObserver
import com.github.brianspace.common.observable.IObservable
import com.github.brianspace.moviebrowser.BR
import com.github.brianspace.moviebrowser.models.IMovieCollection
import com.github.brianspace.moviebrowser.models.Movie
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * View model for movie list, with support for paging.
 * @property movieCollection Interface for accessing model level movie collection.
 * @property viewModelFactory Interface for view model factory.
 */
internal class MoviesViewModel(
    private val movieCollection: IMovieCollection,
    private val viewModelFactory: IViewModelFactory
) : BaseObservable(), IMovieList {

    // region Private Fields

    /**
     * List of movie view models.
     */
    private val movieList = ObservableArrayList<MovieViewModel>()

    /**
     * Observer instance for the model layer list of movies.
     */
    private val movieListObserver = MovieListObserver()

    // endregion

    // region Private Inner Types

    /**
     * Observer for the model layer list of movies.
     */
    private inner class MovieListObserver : ICollectionObserver {
        override
        fun onUpdate(
            observable: IObservable<ICollectionObserver>, action: ICollectionObserver.Action, item: Any?,
            range: List<Any>?
        ) {
            when (action) {
                ICollectionObserver.Action.Clear -> if (!movies.isEmpty()) {
                    movies.clear()
                }
                ICollectionObserver.Action.AppendItem -> if (item != null) {
                    movies.add(viewModelFactory.createMovieViewModel(item as Movie))
                }
                ICollectionObserver.Action.AppendRange -> if (range != null && !range.isEmpty()) {
                    movies.addAll(range.map { viewModelFactory.createMovieViewModel(it as Movie) })
                }
                ICollectionObserver.Action.AddItemToFront -> if (item != null) {
                    movies.add(0, viewModelFactory.createMovieViewModel(item as Movie))
                }
                ICollectionObserver.Action.RemoveItem -> {
                    movies.singleOrNull { it.hasId((item as Movie).id) }?.apply {
                        movies.remove(this)
                    }
                }
                ICollectionObserver.Action.UpdateItem -> {
                }
                else -> {
                }
            }// Not used for now.
        }
    }

    init {
        movieCollection.addObserver(movieListObserver)
    }

    // endregion

    // region Public Overrides

    /**
     * Loading state.
     */
    override var isLoading: Boolean = false
        private set(value) {
            field = value
        }

    override val isLoaded: Boolean
        get() = movies.size > 0

    override val movies: ObservableList<MovieViewModel>
        @Bindable
        get() = movieList

    override fun load(): Completable {
        isLoading = true
        if (movieCollection.isLoaded) {
            movies.addAll(movieCollection.movies.map { viewModelFactory.createMovieViewModel(it) })
            isLoading = false
            notifyPropertyChanged(BR.movies)
            return Completable.complete()
        }

        return movieCollection.load()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLoading = false }
    }

    override fun refresh(): Completable {
        isLoading = true
        movies.clear()
        return movieCollection.refresh()
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLoading = false }
    }

    override fun hasNexPage(): Boolean {
        return movieCollection.hasNexPage()
    }

    override fun loadNextPage(): Completable {
        isLoading = true
        return movieCollection.loadNextPage()
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLoading = false }
    }

    // endregion
}
