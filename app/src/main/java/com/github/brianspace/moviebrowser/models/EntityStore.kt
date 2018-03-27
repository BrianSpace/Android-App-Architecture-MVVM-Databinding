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

import com.github.brianspace.common.objstore.ModelObjectStore
import com.github.brianspace.moviebrowser.repository.IMovieDbService
import com.github.brianspace.moviebrowser.repository.data.MovieData
import javax.inject.Inject

/**
 * Model level entity store.
 */
internal class EntityStore
/**
 * Constructor.
 *
 * @param movieDbService the interface to call Movie DB Web API.
 */
@Inject
constructor(movieDbService: IMovieDbService) : IEntityStore {

    // region Private Fields

    /**
     * Movie model object store.
     */
    private val movieModelObjectStore: ModelObjectStore<Movie, MovieData> =
        ModelObjectStore { data: MovieData -> Movie(movieDbService, this, data) }

    // endregion

    // region Public Overrides

    override fun getMovieModel(movieData: MovieData): Movie {
        return movieModelObjectStore.getOrCreate(movieData)
    }

    override fun findMovieById(id: Int): Movie? {
        return movieModelObjectStore.find(id)
    }

    // endregion
}
