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

package com.github.brianspace.moviebrowser.models;

import android.support.annotation.NonNull;
import com.github.brianspace.common.observable.ICollectionObserver;
import com.github.brianspace.common.observable.IObservable;
import io.reactivex.Completable;
import java.util.List;

/**
 * Interface for paged observable movie list.
 */
public interface IMovieCollection extends IObservable<ICollectionObserver> {

    /**
     * State of data loading.
     * @return true if is data is ready, otherwise false.
     */
    boolean isLoaded();

    /**
     * State of loading.
     * @return true if is loading data, otherwise false.
     */
    boolean isLoading();

    /**
     * Get the list of movies.
     */
    @NonNull
    List<Movie> getMovies();

    /**
     * Load the list.
     * @return RxJava {@code Completable}.
     */
    @NonNull
    Completable load();

    /**
     * Refresh the list.
     * @return RxJava {@code Completable}.
     */
    @NonNull
    Completable refresh();

    /**
     * Whether there is still a next page or not.
     */
    boolean hasNexPage();

    /**
     * Load next page.
     * @return RxJava {@code Completable}.
     */
    @NonNull
    Completable loadNextPage();
}
