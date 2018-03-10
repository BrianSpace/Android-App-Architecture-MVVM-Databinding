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

package com.github.brianspace.moviebrowser.viewmodels;

import android.databinding.Observable;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Interface for list of movie view models.
 */
public interface IMovieList extends Observable {

    /**
     * Get if data is already loaded or not.
     */
    boolean isLoaded();

    /**
     * Get loading state.
     */
    boolean isLoading();

    /**
     * Get the list of movie view models.
     * @return list of movie view models ready for data binding.
     */
    @NonNull
    ObservableList<MovieViewModel> getMovies();

    /**
     * Load data.
     * @return RxJava {@code Completable} result.
     */
    @NonNull
    Completable load();

    /**
     * Refresh data.
     * @return RxJava {@code Completable} result.
     */
    @NonNull
    Completable refresh();

    /**
     * Check if the list has next page or not.
     * @return true if there is still a next page available, otherwise false.
     */
    boolean hasNexPage();

    /**
     * Load the next page.
     * @return RxJava {@code Completable} result.
     */
    @NonNull
    Completable loadNextPage();
}
