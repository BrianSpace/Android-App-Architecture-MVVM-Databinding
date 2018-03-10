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

package com.github.brianspace.moviebrowser.repository.local.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import com.github.brianspace.moviebrowser.repository.data.MovieData;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * ROOM Entity class for the database table to store favorite movies.
 */
@Entity(indices = {@Index("id"), @Index("create_time")})
public class Favorite {

    // region Private Fields

    /**
     * Gson instance for handling JSON strings.
     */
    @Ignore
    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    // endregion

    // region Constructors

    /**
     * Movie ID.
     */
    @PrimaryKey
    public int id;

    /**
     * The time the movie is added to the table.
     */
    @ColumnInfo(name = "create_time")
    public long createTime;

    /**
     * Movie title.
     */
    public String title;

    /**
     * Movie poster image path.
     */
    public String poster;

    /**
     * JSON string of the original movie data.
     */
    /* default */ String json;

    // endregion

    // region Constructors

    /**
     * Default constructor, used by ROOM.
     */
    public Favorite() {
        // do nothing
    }

    /**
     * Create a new Favorite instance from MovieData.
     *
     * @param movie MovieData instance.
     */
    public Favorite(final MovieData movie) {
        this.id = movie.getId();
        this.createTime = System.currentTimeMillis();
        this.title = movie.getTitle();
        this.poster = movie.getPosterPath();
        this.json = gson.toJson(movie);
    }

    // endregion

    // region Public Methods

    /**
     * Create a new instance of MovieData from this object.
     *
     * @return a new instance of MovieData.
     */
    public MovieData toMovie() {
        return gson.fromJson(json, MovieData.class);
    }

    // endregion
}