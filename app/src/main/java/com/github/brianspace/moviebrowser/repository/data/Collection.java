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

/**
 * Movie collection.
 */
@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class Collection {
    private int id;
    private String name;
    private String posterPath;
    private String backdropPath;

    /**
     * Get collection ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Get collection name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the poster path (in the URL).
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * Get the backdrop image path (in the URL).
     */
    public String getBackdropPath() {
        return backdropPath;
    }
}
