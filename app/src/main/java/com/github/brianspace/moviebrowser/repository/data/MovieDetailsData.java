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

import java.util.List;

/**
 * Extra details of a movie.
 */
@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class MovieDetailsData extends MovieData {
    private Collection belongsToCollection;
    private long budget;
    private List<Genre> genres;
    private String homepage;
    private String imdbId;
    private List<Company> productionCompanies;
    private List<Country> productionCountries;
    private long revenue;
    private long runtime;
    private List<Language> spokenLanguages;
    private String status;
    private String tagline;

    /**
     * Get the {@link com.github.brianspace.moviebrowser.repository.data.Collection Collection} the movie belongs to.
     */
    public Collection getBelongsToCollection() {
        return belongsToCollection;
    }

    /**
     * Get the movie's budget.
     */
    public long getBudget() {
        return budget;
    }

    /**
     * Get the list of {@link Genre Genres} of the movie.
     */
    public final Iterable<Genre> getGenres() {
        return genres;
    }

    /**
     * Get the movie's homepage.
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * Get the movie's ID in IMDB.
     */
    public String getImdbId() {
        return imdbId;
    }

    /**
     * Get the list of production {@link Company Companies} of the movie.
     */
    public final Iterable<Company> getProductionCompanies() {
        return productionCompanies;
    }

    /**
     * Get the list of production {@link Country Countries} of the movie.
     */
    public final Iterable<Country> getProductionCountries() {
        return productionCountries;
    }

    /**
     * Get the movie's revenue.
     */
    public long getRevenue() {
        return revenue;
    }

    /**
     * Get the movie's runtime.
     */
    public long getRuntime() {
        return runtime;
    }

    /**
     * Get the list of spoken {@link Language Languages} of the movie.
     */
    public final Iterable<Language> getSpokenLanguages() {
        return spokenLanguages;
    }

    /**
     * Get the movie's status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Get the movie's tagline.
     */
    public String getTagline() {
        return tagline;
    }
}
