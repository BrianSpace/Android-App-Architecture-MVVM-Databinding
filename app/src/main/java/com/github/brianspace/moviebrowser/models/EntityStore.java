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
import android.support.annotation.Nullable;
import com.github.brianspace.common.objstore.ModelObjectStore;
import com.github.brianspace.moviebrowser.repository.IMovieDbService;
import com.github.brianspace.moviebrowser.repository.data.MovieData;
import javax.inject.Inject;

/**
 * Model level entity store.
 */
class EntityStore implements IEntityStore {

    // region Private Fields

    /**
     * Movie model object store.
     */
    private final ModelObjectStore<Movie, MovieData> movieModelObjectStore;

    // endregion

    // region Constructors

    /**
     * Constructor.
     *
     * @param movieDbService the interface to call Movie DB Web API.
     */
    @Inject
    /* default */ EntityStore(@NonNull final IMovieDbService movieDbService) {
        movieModelObjectStore = new ModelObjectStore<>(data -> new Movie(movieDbService, this, data));
    }

    // endregion

    // region Public Overrides

    @NonNull
    @Override
    public Movie getMovieModel(@NonNull final MovieData movieData) {
        return movieModelObjectStore.getOrCreate(movieData);
    }

    @Nullable
    @Override
    public Movie findMovieById(final int id) {
        return movieModelObjectStore.find(id);
    }

    // endregion
}
