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

import com.google.gson.annotations.SerializedName

/**
 * System wide configuration information for TMDb API.
 * @see [Configuration](https://developers.themoviedb.org/3/configuration/get-api-configuration)
 */
class Configuration {
    /**
     * The configuration for images.
     */
    @SerializedName("images")
    val imageConfig: ImageConfig? = null

    /**
     * The list of keys for change feed from TMDb.
     */
    val changeKeys: List<String>? = null

    /**
     * Configuration for images.
     * To build an image URL, you will need 3 pieces of data. The base_url, size and file_path.
     * Simply combine them all and you will have a fully qualified URL. Here’s an example URL:
     * https://image.tmdb.org/t/p/w500/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg
     */
    class ImageConfig {
        /**
         * The image base URL, which is used to construct the full URL for an image.
         */
        val baseUrl: String? = null

        /**
         * The HTTPS image base URL.
         */
        val secureBaseUrl: String? = null

        /**
         * The supported sizes for the movie backdrop images. Example:
         * [w300, w780, w1280, original]
         */
        val backdropSizes: List<String>? = null

        /**
         * The supported sizes for the logo images. Example:
         * [w45, w92, w154, w185, w300, w500, original]
         */
        val logoSizes: List<String>? = null

        /**
         * The supported sizes for the poster images. Example:
         * [w92, w154, w185, w342, w500, w780, original]
         */
        val posterSizes: List<String>? = null

        /**
         * The supported sizes for the profile images. Example:
         * [w45, w185, h632, original]
         */
        val profileSizes: List<String>? = null

        /**
         * The supported sizes for the still images. Example:
         * [w92, w185, w300, original]
         */
        val stillSizes: List<String>? = null
    }
}
