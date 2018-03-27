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

package com.github.brianspace.moviebrowser.models

/**
 * Interface for image configuration.
 * Different image type supplies different set of image widths.
 */
interface IImageConfig {

    /**
     * Get the base URL of the movie backdrop images for a specified width.
     */
    fun getBackdropBaseUrl(width: Int): String

    /**
     * Get the base URL of the poster images for a specified width.
     */
    fun getPosterBaseUrl(width: Int): String
}
