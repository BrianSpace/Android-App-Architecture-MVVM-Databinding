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

import com.github.brianspace.common.observable.CollectionObservableBase
import com.github.brianspace.common.observable.ICollectionObserver.Action
import com.github.brianspace.moviebrowser.repository.IFavoriteStore
import io.reactivex.Completable
import io.reactivex.Single
import java.security.InvalidParameterException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The list of favorite movies.
 */
@Singleton
internal class FavoriteMovieCollection
@Inject
constructor(
    /**
     * Interface for favorite database.
     */
    private val favoriteStore: IFavoriteStore,
    /**
     * Interface for model entity store.
     */
    private val entityStore: IEntityStore
) : CollectionObservableBase(), IFavoriteMovieCollection {

    // region Protected Properties

    /**
     * The list of favorite movies.
     */
    private val movieList: MutableList<Movie> = ArrayList()

    // endregion

    // region Public Overrides

    override var isLoading: Boolean = false
        private set(value) {
            field = value
        }

    override val isLoaded: Boolean
        get() = !movieList.isEmpty()

    override val movies: List<Movie>
        get() = movieList

    override fun load(): Completable {
        isLoading = true
        return favoriteStore.allFavoriteMovies
            .map { favoriteList ->
                for (movie in favoriteList) {
                    val movieModel = entityStore.getMovieModel(movie)
                    movieModel.isFavorite = true
                    movieList.add(movieModel)
                    setChanged()
                    notifyObservers(Action.AppendItem, movieModel, null)
                }

                Irrelevant.INSTANCE
            }
            .toCompletable()
            .doFinally { isLoading = false }
    }

    override fun refresh(): Completable {
        movieList.clear()
        setChanged()
        notifyObservers(Action.Clear, null, null)
        return load()
    }

    override fun hasNexPage(): Boolean {
        return false
    }

    override fun loadNextPage(): Completable {
        return Completable.complete()
    }

    override fun addToFavorite(movie: Movie): Single<Boolean> {
        val found = findModelWithSameId(movie)
        if (found != null || movie.isFavorite) {
            throw InvalidParameterException("The movie is already in favorite list!")
        }

        return favoriteStore.addFavoriteMovie(movie.movieData)
            .map { result ->
                if (result) {
                    movieList.add(0, movie)
                    movie.isFavorite = true
                    setChanged()
                    notifyObservers(Action.AddItemToFront, movie, null)
                }
                result
            }
    }

    override fun removeFromFavorite(movie: Movie): Single<Boolean> {
        val found = findModelWithSameId(movie)

        if (found != null && movie.isFavorite) {
            return favoriteStore.deleteFavoriteMovie(movie.movieData).map { result ->
                if (result) {
                    movieList.remove(found)
                    movie.isFavorite = false
                    setChanged()
                    notifyObservers(Action.RemoveItem, movie, null)
                }

                result
            }
        }

        throw InvalidParameterException("The movie is not a favorite movie!")
    }

    // endregion

    // region Internal Methods

    /**
     * Clear all favorite movies. Used by [DataCleaner].
     * For simplicity, database is not cleared here but in the cleaner.
     */
    internal fun clear() {
        for (item in movieList) {
            item.isFavorite = false
        }

        movieList.clear()
        setChanged()
        notifyObservers(Action.Clear, null, null)
    }

    // endregion

    // region Private Methods

    private fun findModelWithSameId(movie: Movie): Movie? {
        return movieList.singleOrNull { it.id == movie.id }
    }

    // endregion
}
