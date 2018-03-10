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

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.github.brianspace.moviebrowser.repository.IConfigStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Immutable value object to handle TMDb image size configurations, used by {@link TmdbConfig}.
 *
 * @see com.github.brianspace.moviebrowser.repository.data.Configuration
 */
/* default */ final class ImageSizesConfig {
    // region Private Constants

    /**
     * String format to combine base URL, width path and ending backslash.
     */
    private static final String URL_FORMAT = "%s%s/";

    /**
     * Maximum width value, used for "original" width string.
     */
    private static final int WIDTH_MAX = Integer.MAX_VALUE;
    /**
     * Used for invalid width value.
     */
    private static final int WIDTH_INVALID = -1;

    /**
     * Separator for concatenated size strings.
     */
    private static final char SIZE_SEPARATOR = ':';

    /**
     * Regular expression to split the concatenated size string.
     */
    private static final String SIZE_SEPARATOR_REGEX = ":";

    /**
     * Path for original image size.
     */
    private static final String ORIGINAL = "original";

    /**
     * Default image size config. Use original images.
     */
    private static final String[] DEFAULT_SIZES = {ORIGINAL};

    /**
     * Default image size strings.
     */
    private static final List<String> DEFAULT_SIZE_STRINGS = Arrays.asList(DEFAULT_SIZES);

    // endregion

    // region Public Constants
    // endregion

    // region Private Fields

    /**
     * Base URL for images.
     */
    private final String imageBaseUrl;

    /**
     * Concatenated string of image sizes.
     * Used to save to config store and compare with new configuration.
     */
    private final String concatSizes;

    /**
     * List of ImageSize objects for movie images.
     */
    private final List<ImageSize> imageSizes = new ArrayList<>();

    // endregion

    // region Protected Fields
    // endregion

    // region Private Types

    /**
     * Immutable value object to parse the TMDb image size strings.
     */
    private static final class ImageSize {

        /**
         * Size string (as part of the full image URL).
         */
        private final String sizeString;

        /**
         * Corresponding image width.
         */
        private final int width;

        /**
         * Constructor.
         *
         * @param size size string from TMDb configuration API.
         */
        public ImageSize(@NonNull final String size) {
            int widthResult;
            sizeString = size;
            if (ORIGINAL.equals(size)) {
                widthResult = WIDTH_MAX;
            } else {
                // Remove the "w" prefix.
                final String widthString = size.substring(1);
                try {
                    widthResult = Integer.parseInt(widthString);
                } catch (final NumberFormatException e) {
                    widthResult = WIDTH_INVALID;
                }
            }

            width = widthResult;
        }

        /**
         * Get size string (as part of the full image URL).
         */
        public String getSizeString() {
            return sizeString;
        }

        /**
         * Get corresponding image width, Integer.MAX_VALUE for "original" size.
         */
        public int getWidth() {
            return width;
        }
    }

    // endregion

    // region Constructors

    /**
     * Create a new instance of ImageSizesConfig.
     *
     * @param imageBaseUrl base URL for TMDb images.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private ImageSizesConfig(@NonNull final String imageBaseUrl, @NonNull final Iterable<String> sizeStrings) {
        this.imageBaseUrl = imageBaseUrl;
        concatSizes = getConcatenatedString(sizeStrings);
        for (final String size : sizeStrings) {
            imageSizes.add(new ImageSize(size));
        }
    }

    // endregion

    // region Public Static Methods

    /**
     * Load value from config store.
     *
     * @param imageBaseUrl TMDb image base URL.
     * @param configStore interface for configuration store.
     * @param configKey config key for config store.
     * @return The new instance of ImageSizesConfig.
     */
    /* default */ static ImageSizesConfig loadFromConfigStore(@NonNull final String imageBaseUrl,
            @NonNull final IConfigStore configStore, @NonNull final String configKey) {
        final String sizeString = configStore.getConfigItem(configKey);
        return new ImageSizesConfig(imageBaseUrl,
                sizeString == null ? DEFAULT_SIZE_STRINGS : breakConcatenatedString(sizeString));
    }

    /**
     * Create new instance from image size list.
     *
     * @param imageBaseUrl TMDb image base URL.
     * @param sizeStrings new value for image size.
     * @return The new instance of ImageSizesConfig.
     */
    /* default */ static ImageSizesConfig createFromImageSizes(@NonNull final String imageBaseUrl,
            @NonNull final Iterable<String> sizeStrings) {
        return new ImageSizesConfig(imageBaseUrl, sizeStrings);
    }

    /**
     * Save to config store.
     *
     * @param config the ImageSizesConfig instance.
     * @param configStore interface for configuration store.
     * @param configKey config key for config store.
     */
    /* default */ static void saveToConfigStore(@NonNull final ImageSizesConfig config,
            @NonNull final IConfigStore configStore, @NonNull final String configKey) {
        configStore.saveConfigItem(configKey, config.concatSizes);
    }

    // endregion

    // region Public Overrides

    // endregion

    // region Public Methods

    /**
     * Compare ImageSizesConfig instances.
     *
     * @param other a ImageSizesConfig instance.
     * @return True if the input is not null and has the same values, otherwise false.
     */
    public boolean sameAs(final ImageSizesConfig other) {
        return other != null && imageBaseUrl.equals(other.imageBaseUrl) && concatSizes.equals(other.concatSizes);
    }

    /**
     * Get the base URL of the movie images for a specified width.
     *
     * @param width desired image width
     * @return the image base URL for the specified width.
     */
    /* default */ String getImageBaseUrl(final int width) {
        for (final ImageSize item : imageSizes) {
            if (item.getWidth() >= width) {
                return String.format(Locale.ROOT, URL_FORMAT, imageBaseUrl, item.getSizeString());
            }
        }

        return Constants.DEFAULT_IMAGE_PATH;
    }

    // endregion

    // region Protected Methods
    // endregion

    // region Private Methods

    @VisibleForTesting
    @NonNull
    /* default */ static String getConcatenatedString(final Iterable<String> sizeStrings) {
        final StringBuilder builder = new StringBuilder();
        for (final String item : sizeStrings) {
            builder.append(item);
            builder.append(SIZE_SEPARATOR);
        }

        // Remove last separator.
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    @VisibleForTesting
    @NonNull
    /* default */ static List<String> breakConcatenatedString(final String concatenated) {
        return Arrays.asList(concatenated.split(SIZE_SEPARATOR_REGEX));
    }

    // endregion

    // region Inner interfaces or classes
    // endregion
}