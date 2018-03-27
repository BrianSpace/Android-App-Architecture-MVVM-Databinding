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

package com.github.brianspace.moviebrowser.repository

/**
 * Interface for configuration repository.
 */
interface IConfigStore {

    /**
     * Get a configuration item by the specified key.
     *
     * @param key the key used to retrieve the config item.
     * @return config string corresponding to the key.
     */
    fun getConfigItem(key: String): String?

    /**
     * Get a configuration set by the specified key.
     *
     * @param key the key used to retrieve the config set.
     * @return config set corresponding to the key.
     */
    fun getConfigSet(key: String): Set<String>?

    /**
     * Save config.
     * @param key   config key.
     * @param value config value.
     */
    fun saveConfigItem(key: String, value: String)

    /**
     * Save config set.
     * @param key    config set key.
     * @param values config set values.
     */
    fun saveConfigSet(key: String, values: Set<String>)

    companion object {

        /**
         * Key for image base URL from TMDb.
         */
        const val KEY_TMDB_IMAGE_BASE_URL = "TMDB_IMAGE_BASE_URL"

        /**
         * Key for the sizes of backdrop images from TMDb.
         */
        const val KEY_TMDB_BACKDROP_SIZES = "TMDB_BACKDROP_SIZES"

        /**
         * Key for the sizes of poster images from TMDb.
         */
        const val KEY_TMDB_POSTER_SIZES = "TMDB_POSTER_SIZES"
    }
}
