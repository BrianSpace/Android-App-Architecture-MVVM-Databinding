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

import android.databinding.Observable
import android.databinding.ObservableList
import io.reactivex.Completable

/**
 * Interface for list of movie view models.
 */
interface IMovieList : Observable {

    /**
     * Get if data is already loaded or not.
     */
    val isLoaded: Boolean

    /**
     * Get loading state.
     */
    val isLoading: Boolean

    /**
     * Get the list of movie view models.
     * @return list of movie view models ready for data binding.
     */
    val movies: ObservableList<MovieViewModel>

    /**
     * Load data.
     * @return RxJava `Completable` result.
     */
    fun load(): Completable

    /**
     * Refresh data.
     * @return RxJava `Completable` result.
     */
    fun refresh(): Completable

    /**
     * Check if the list has next page or not.
     * @return true if there is still a next page available, otherwise false.
     */
    fun hasNexPage(): Boolean

    /**
     * Load the next page.
     * @return RxJava `Completable` result.
     */
    fun loadNextPage(): Completable
}
