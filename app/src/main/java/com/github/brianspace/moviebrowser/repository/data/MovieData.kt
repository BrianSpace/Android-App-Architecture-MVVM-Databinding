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

package com.github.brianspace.moviebrowser.repository.data

import com.github.brianspace.common.objstore.IEntity
import com.google.gson.Gson

/**
 * Movie data (without details).
 */
open class MovieData : IEntity {
    /**
     * The movie ID.
     */
    override val id: Int = 0

    /**
     * Check if the movie is an adult movie.
     */
    val isAdult: Boolean = false

    /**
     * The title of the movie.
     */
    val title: String? = null

    /**
     * The original title of the movie.
     */
    val originalTitle: String? = null

    /**
     * The original language of the movie.
     */
    val originalLanguage: String? = null

    /**
     * The overview of the movie.
     */
    val overview: String? = null

    /**
     * The release date of the movie.
     */
    val releaseDate: String? = null

    /**
     * The movie poster image path.
     */
    val posterPath: String? = null

    /**
     * The movie backdrop image path.
     */
    val backdropPath: String? = null

    /**
     * The list of [Genre] IDs of the movie.
     */
    val genreIds: IntArray? = null

    /**
     * TBD.
     */
    val isVideo: Boolean = false

    /**
     * The popularity of the movie.
     */
    val popularity: Float = 0.toFloat()

    /**
     * The average vote of the movie.
     */
    val voteAverage: Float = 0.toFloat()

    /**
     * The vote count of the movie.
     */
    val voteCount: Int = 0

    /**
     * Check if the data is valid for our application: has an positive ID, both the title and poster path are not null.
     * @return true if it is valid, otherwise false.
     */
    val isValid: Boolean
        get() = id > 0 && title != null && posterPath != null

    /**
     * Return the JSON representation of this object. Just for logging.
     */
    override fun toString(): String {
        return Gson().toJson(this)
    }
}
