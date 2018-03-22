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

import com.github.brianspace.moviebrowser.repository.IMovieDbService
import com.github.brianspace.moviebrowser.repository.data.MovieData
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope
import io.reactivex.Single
import javax.inject.Inject

/**
 * The now playing movie list.
 */
internal class NowPlayingMovieCollection
/**
 * Create a new instance of now playing movie list.
 *
 * @param movieDbService interface for accessing TMDb Web API.
 * @param entityStore interface for model entity store.
 */
@Inject
constructor(
    movieDbService: IMovieDbService,
    entityStore: IEntityStore
) : MovieCollection(movieDbService, entityStore) {

    // endregion

    // region Protected Methods

    override val firstPage: Single<PagingEnvelope<MovieData>>
        get() = movieDbService.getMovieNowPlaying(null)

    override fun getNextPage(prev: PagingEnvelope<MovieData>?): Single<PagingEnvelope<MovieData>> {
        return movieDbService.getMovieNowPlaying(prev)
    }

    // endregion
}
