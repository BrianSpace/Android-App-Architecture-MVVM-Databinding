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

import com.github.brianspace.moviebrowser.BuildConfig;

/**
 * Constants for repository sub-module.
 */
public class Constants {

    /**
     * Cache size for the HTTP requests.
     */
    public static final int HTTP_CACHE_SIZE = 10 * 1024 * 1024;

    /**
     * Database Name (for favorite movies).
     */
    public static final String DATABASE_NAME = "MovieDB";

    /**
     * Timeout (in seconds) for connecting TMDb Web API. Use smaller value for debug mode.
     */
    public static final int TMDB_API_TIMEOUT_CONNECT = BuildConfig.DEBUG ? 1 : 5;

    /**
     * Timeout (in seconds) for reading from TMDb Web API. Use smaller value for debug mode.
     */
    public static final int TMDB_API_TIMEOUT_READ = BuildConfig.DEBUG ? 1 : 5;
}