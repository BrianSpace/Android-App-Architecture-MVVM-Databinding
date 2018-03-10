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

import com.google.gson.annotations.SerializedName;

/**
 * Country.
 */
@SuppressWarnings("PMD.CommentRequired")
public class Country {
    @SerializedName("iso_3166_1")
    private String shortName;
    private String name;

    /**
     * Get the short country name.
     * @see <a href="https://en.wikipedia.org/wiki/ISO_3166-1">ISO 3166-1</a>

     * @return the country name in ISO 3166-1 format.
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Get the country name.
     */
    public String getName() {
        return name;
    }
}
