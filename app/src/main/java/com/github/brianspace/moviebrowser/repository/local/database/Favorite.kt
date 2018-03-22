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

package com.github.brianspace.moviebrowser.repository.local.database

import android.arch.persistence.room.*
import com.github.brianspace.moviebrowser.repository.data.MovieData
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder

/**
 * Gson instance for handling JSON strings.
 */
@Ignore
private val gson = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create()

/**
 * ROOM Entity class for the database table to store favorite movies.
 */
@Entity(indices = [Index("id"), Index("create_time")])
class Favorite() {
    // region Properties

    /**
     * Movie ID.
     */
    @PrimaryKey
    var id: Int = 0

    /**
     * The time the movie is added to the table.
     */
    @ColumnInfo(name = "create_time")
    var createTime: Long = 0

    /**
     * Movie title.
     */
    var title: String? = null

    /**
     * Movie poster image path.
     */
    var poster: String? = null

    /**
     * JSON string of the original movie data.
     */
    var json: String? = null

    // endregion

    // region Constructors

    /**
     * Create a new Favorite instance from MovieData.
     *
     * @param movie MovieData instance.
     */
    constructor(movie: MovieData) : this() {
        id = movie.id
        createTime = System.currentTimeMillis()
        title = movie.title
        poster = movie.posterPath
        json = gson.toJson(movie)
    }

    // endregion

    // region Public Methods

    /**
     * Create a new instance of MovieData from this object.
     *
     * @return a new instance of MovieData.
     */
    fun toMovie(): MovieData {
        return gson.fromJson(json, MovieData::class.java)
    }

    // endregion
}