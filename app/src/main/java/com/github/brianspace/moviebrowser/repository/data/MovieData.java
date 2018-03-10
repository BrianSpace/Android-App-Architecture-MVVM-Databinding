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

package com.github.brianspace.moviebrowser.repository.data;

import com.github.brianspace.common.objstore.IEntity;
import com.google.gson.Gson;

/**
 * Movie data (without details).
 */
@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class MovieData implements IEntity {
    private int id;
    private boolean adult;
    private String title;
    private String originalTitle;
    private String originalLanguage;
    private String overview;
    private String releaseDate;
    private String posterPath;
    private String backdropPath;
    private int[] genreIds;
    private boolean video;
    private float popularity;
    private float voteAverage;
    private int voteCount;

    /**
     * Check if the movie is an adult movie.
     */
    public boolean isAdult() {
        return adult;
    }

    /**
     * Get the movie backdrop image path.
     */
    public String getBackdropPath() {
        return backdropPath;
    }

    /**
     * Get the list of {@link Genre Genre} IDs of the movie.
     */
    public int[] getGenreIds() {
        return genreIds.clone();
    }

    /**
     * Get the movie ID.
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Get the original language of the movie.
     */
    public String getOriginalLanguage() {
        return originalLanguage;
    }

    /**
     * Get the original title of the movie.
     */
    public String getOriginalTitle() {
        return originalTitle;
    }

    /**
     * Get the overview of the movie.
     */
    public String getOverview() {
        return overview;
    }

    /**
     * Get the release date of the movie.
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Get the movie poster image path.
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * Get the popularity of the movie.
     */
    public float getPopularity() {
        return popularity;
    }

    /**
     * Get the title of the movie.
     */
    public String getTitle() {
        return title;
    }

    /**
     * TBD.
     */
    public boolean isVideo() {
        return video;
    }

    /**
     * Get the average vote of the movie.
     */
    public float getVoteAverage() {
        return voteAverage;
    }

    /**
     * Get the vote count of the movie.
     */
    public int getVoteCount() {
        return voteCount;
    }

    /**
     * Check if the data is valid for our application: has an positive ID, both the title and poster path are not null.
     * @return true if it is valid, otherwise false.
     */
    public boolean isValid() {
        return id > 0 && title != null && posterPath != null;
    }

    /**
     * Return the JSON representation of this object.
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
