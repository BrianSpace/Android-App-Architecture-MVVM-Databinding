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

package com.github.brianspace.moviebrowser.models;

/**
 * Constants for model layer.
 */
class Constants {

    /**
     * Constants: backslash.
     */
    /* default */ static final String BACK_SLASH = "/";

    /**
     * Default image base URL for TMDb images.
     */
    /* default */ static final String DEFAULT_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    /**
     * Default image path in the URL, use original image size.
     */
    /* default */ static final String DEFAULT_IMAGE_PATH = DEFAULT_IMAGE_BASE_URL + "original/";
}