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

import android.support.annotation.VisibleForTesting
import com.github.brianspace.moviebrowser.repository.IConfigStore
import java.util.ArrayList
import java.util.Arrays
import java.util.Locale

// region Private Constants

/**
 * String format to combine base URL, width path and ending backslash.
 */
private const val URL_FORMAT = "%s%s/"

/**
 * Maximum width value, used for "original" width string.
 */
private const val WIDTH_MAX = Integer.MAX_VALUE

/**
 * Used for invalid width value.
 */
private const val WIDTH_INVALID = -1

/**
 * Separator for concatenated size strings.
 */
private const val SIZE_SEPARATOR = ':'

/**
 * Regular expression to split the concatenated size string.
 */
private val sizeSeparatorRegex = ":".toRegex()

/**
 * Path for original image size.
 */
private const val ORIGINAL = "original"

// endregion

/**
 * Immutable value object to handle TMDb image size configurations, used by [TmdbConfig].
 *
 * @see com.github.brianspace.moviebrowser.repository.data.Configuration
 */
internal class ImageSizesConfig
private constructor(
    /**
     * Base URL for images.
     */
    private val imageBaseUrl: String,
    /**
     * Collection of size strings.
     */
    sizeStrings: Iterable<String>
) {

    // region Private Properties

    /**
     * Concatenated string of image sizes.
     * Used to save to config store and compare with new configuration.
     */
    private val concatSizes: String

    /**
     * List of ImageSize objects for movie images.
     */
    private val imageSizes = ArrayList<ImageSize>()

    // endregion

    // region Private Types

    /**
     * Immutable value object to parse the TMDb image size strings.
     */
    private class ImageSize(
        /**
         * Size string (as part of the full image URL).
         */
        val sizeString: String
    ) {

        /**
         * Corresponding image width.
         */
        val width: Int = if (ORIGINAL == sizeString) {
            WIDTH_MAX
        } else {
            // Remove the "w" prefix.
            val widthString = sizeString.substring(1)
            try {
                Integer.parseInt(widthString)
            } catch (e: NumberFormatException) {
                WIDTH_INVALID
            }
        }
    }

    init {
        concatSizes = getConcatenatedString(sizeStrings)
        for (size in sizeStrings) {
            imageSizes.add(ImageSize(size))
        }
    }

    // endregion

    // region Public Methods

    /**
     * Compare ImageSizesConfig instances.
     *
     * @param other a ImageSizesConfig instance.
     * @return True if the input is not null and has the same values, otherwise false.
     */
    fun sameAs(other: ImageSizesConfig?): Boolean {
        return other != null && imageBaseUrl == other.imageBaseUrl && concatSizes == other.concatSizes
    }

    /**
     * Get the base URL of the movie images for a specified width.
     *
     * @param width desired image width
     * @return the image base URL for the specified width.
     */
    fun getImageBaseUrl(width: Int): String {
        for (item in imageSizes) {
            if (item.width >= width) {
                return String.format(Locale.ROOT, URL_FORMAT, imageBaseUrl, item.sizeString)
            }
        }

        return Constants.DEFAULT_IMAGE_PATH
    }

    companion object {
        // region Public Static Methods

        /**
         * Load value from config store.
         *
         * @param imageBaseUrl TMDb image base URL.
         * @param configStore interface for configuration store.
         * @param configKey config key for config store.
         * @return The new instance of ImageSizesConfig.
         */
        fun loadFromConfigStore(
            imageBaseUrl: String,
            configStore: IConfigStore, configKey: String
        ): ImageSizesConfig {
            val sizeString = configStore.getConfigItem(configKey)
            return ImageSizesConfig(
                imageBaseUrl,
                if (sizeString == null) listOf(ORIGINAL) else breakConcatenatedString(sizeString)
            )
        }

        /**
         * Create new instance from image size list.
         *
         * @param imageBaseUrl TMDb image base URL.
         * @param sizeStrings new value for image size.
         * @return The new instance of ImageSizesConfig.
         */
        fun createFromImageSizes(
            imageBaseUrl: String,
            sizeStrings: Iterable<String>
        ): ImageSizesConfig {
            return ImageSizesConfig(imageBaseUrl, sizeStrings)
        }

        /**
         * Save to config store.
         *
         * @param config the ImageSizesConfig instance.
         * @param configStore interface for configuration store.
         * @param configKey config key for config store.
         */
        fun saveToConfigStore(
            config: ImageSizesConfig,
            configStore: IConfigStore, configKey: String
        ) {
            configStore.saveConfigItem(configKey, config.concatSizes)
        }

        // endregion

        // region Private Methods

        @VisibleForTesting
        fun getConcatenatedString(sizeStrings: Iterable<String>): String {
            val builder = StringBuilder()
            for (item in sizeStrings) {
                builder.append(item)
                builder.append(SIZE_SEPARATOR)
            }

            // Remove last separator.
            if (builder.isNotEmpty()) {
                builder.deleteCharAt(builder.length - 1)
            }

            return builder.toString()
        }

        @VisibleForTesting
        fun breakConcatenatedString(concatenated: String): List<String> {
            return concatenated.split(sizeSeparatorRegex)
        }
    }

    // endregion
}
