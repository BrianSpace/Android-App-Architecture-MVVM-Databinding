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

import com.github.brianspace.common.observable.ICollectionObserver
import com.github.brianspace.common.observable.IObservable
import io.reactivex.Completable

/**
 * Interface for paged observable movie list.
 */
interface IMovieCollection : IObservable<ICollectionObserver> {

    /**
     * State of data loading.
     * @return true if is data is ready, otherwise false.
     */
    val isLoaded: Boolean

    /**
     * State of loading.
     * @return true if is loading data, otherwise false.
     */
    val isLoading: Boolean

    /**
     * Get the list of movies.
     */
    val movies: List<Movie>

    /**
     * Load the list.
     * @return RxJava `Completable`.
     */
    fun load(): Completable

    /**
     * Refresh the list.
     * @return RxJava `Completable`.
     */
    fun refresh(): Completable

    /**
     * Whether there is still a next page or not.
     */
    fun hasNexPage(): Boolean

    /**
     * Load next page.
     * @return RxJava `Completable`.
     */
    fun loadNextPage(): Completable
}
