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

package com.github.brianspace.moviebrowser.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Set;

/**
 * Interface for configuration repository.
 */
public interface IConfigStore {

    /**
     * Key for image base URL from TMDb.
     */
    String KEY_TMDB_IMAGE_BASE_URL = "TMDB_IMAGE_BASE_URL";

    /**
     * Key for the sizes of backdrop images from TMDb.
     */
    String KEY_TMDB_BACKDROP_SIZES = "TMDB_BACKDROP_SIZES";

    /**
     * Key for the sizes of poster images from TMDb.
     */
    String KEY_TMDB_POSTER_SIZES = "TMDB_POSTER_SIZES";


    /**
     * Get a configuration item by the specified key.
     *
     * @param key the key used to retrieve the config item.
     * @return config string corresponding to the key.
     */
    @Nullable
    String getConfigItem(@NonNull String key);

    /**
     * Get a configuration set by the specified key.
     *
     * @param key the key used to retrieve the config set.
     * @return config set corresponding to the key.
     */
    @Nullable
    Set<String> getConfigSet(@NonNull String key);

    /**
     * Save config.
     * @param key   config key.
     * @param value config value.
     */
    void saveConfigItem(@NonNull String key, @NonNull String value);


    /**
     * Save config set.
     * @param key    config set key.
     * @param values config set values.
     */
    void saveConfigSet(@NonNull String key, @NonNull Set<String> values);
}
