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

/**
 * Extra details of a movie.
 */
class MovieDetailsData : MovieData() {
    /**
     * The [Collection][com.github.brianspace.moviebrowser.repository.data.Collection] the movie belongs to.
     */
    val belongsToCollection: Collection? = null

    /**
     * The movie's budget.
     */
    val budget: Long = 0

    /**
     * The list of [Genres][Genre] of the movie.
     */
    val genres: List<Genre>? = null

    /**
     * The movie's homepage.
     */
    val homepage: String? = null

    /**
     * The movie's ID in IMDB.
     */
    val imdbId: String? = null

    /**
     * The list of production [Companies][Company] of the movie.
     */
    val productionCompanies: List<Company>? = null

    /**
     * The list of production [Countries][Country] of the movie.
     */
    val productionCountries: List<Country>? = null

    /**
     * The movie's revenue.
     */
    val revenue: Long = 0

    /**
     * The movie's runtime.
     */
    val runtime: Long = 0

    /**
     * The list of spoken [Languages][Language] of the movie.
     */
    val spokenLanguages: List<Language>? = null

    /**
     * The movie's status.
     */
    val status: String? = null

    /**
     * The movie's tagline.
     */
    val tagline: String? = null
}
