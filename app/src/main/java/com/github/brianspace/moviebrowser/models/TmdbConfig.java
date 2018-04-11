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

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;
import com.github.brianspace.moviebrowser.repository.IConfigStore;
import com.github.brianspace.moviebrowser.repository.IMovieDbService;
import com.github.brianspace.moviebrowser.repository.data.Configuration.ImageConfig;
import javax.inject.Inject;

/**
 * Configuration used in this app, from TMDb Web API.
 * For model layer internal use.
 */
class TmdbConfig implements IImageConfig {
    // region Private Constants
    /**
     * Tag for logcat.
     */
    private static final String TAG = TmdbConfig.class.getSimpleName();

    // endregion

    // region Private Fields

    /**
     * Interface for accessing TMDb Web API.
     */
    private final IMovieDbService movieDbService;

    /**
     * Interface for configuration store.
     */
    private final IConfigStore configStore;

    /**
     * Configuration builder for backdrop images.
     */
    private final ImageSizeConfigBuilder backdropSizeConfigBuilder;

    /**
     * Configuration builder for poster images.
     */
    private final ImageSizeConfigBuilder posterSizeConfigBuilder;

    /**
     * Base URL for images.
     */
    private String imageBaseUrl = Constants.DEFAULT_IMAGE_BASE_URL;

    /**
     * Configuration for movie backdrop images. A new instance will be created when value changes.
     */
    private ImageSizesConfig backdropSizeConfig;

    /**
     * Configuration for movie poster images. A new instance will be created when value changes.
     */
    private ImageSizesConfig posterSizeConfig;

    // endregion

    // region Private Types

    /**
     * Class to help the creation of ImageSizesConfig.
     */
    private class ImageSizeConfigBuilder {

        /**
         * Key to save & load config value from config store.
         */
        private final String configKey;

        /**
         * Create a new instance for a specific image type.
         *
         * @param configKey Key to save & load config value from config store.
         */
        /* default */ ImageSizeConfigBuilder(@NonNull final String configKey) {
            this.configKey = configKey;
        }

        /**
         * Load from config store.
         *
         * @return a new ImageSizesConfig instance.
         */
        /* default */ ImageSizesConfig loadFromConfigStore() {
            return ImageSizesConfig.loadFromConfigStore(imageBaseUrl, configStore, configKey);
        }

        /**
         * Update config and save the new value if needed.
         *
         * @param originalConfig the original ImageSizesConfig instance.
         * @param sizeStrings new image size strings.
         * @return a new instance of ImageSizesConfig if the new config is different from the original one, otherwise
         *         the original instance is returned.
         */
        /* default */ ImageSizesConfig updateAndSave(final ImageSizesConfig originalConfig,
                final Iterable<String> sizeStrings) {
            final ImageSizesConfig newConfig = ImageSizesConfig.createFromImageSizes(imageBaseUrl, sizeStrings);
            final boolean changed = !originalConfig.sameAs(newConfig);
            if (changed) {
                ImageSizesConfig.saveToConfigStore(newConfig, configStore, configKey);
            }

            return changed ? newConfig : originalConfig;
        }
    }

    // endregion

    // region Constructors

    /**
     * Create a new instance of the TmdbConfig class.
     *
     * @param movieDbService TMDb Web API interface.
     * @param configStore interface for configuration store.
     */
    @Inject
    /* default */ TmdbConfig(@NonNull final IMovieDbService movieDbService, @NonNull final IConfigStore configStore) {
        this.movieDbService = movieDbService;
        this.configStore = configStore;
        backdropSizeConfigBuilder = new ImageSizeConfigBuilder(IConfigStore.KEY_TMDB_BACKDROP_SIZES);
        posterSizeConfigBuilder = new ImageSizeConfigBuilder(IConfigStore.KEY_TMDB_POSTER_SIZES);
        readFromConfigStore();
    }

    // endregion

    // region Public Overrides

    @Override
    public final String getBackdropBaseUrl(final int width) {
        return backdropSizeConfig.getImageBaseUrl(width);
    }

    @Override
    public final String getPosterBaseUrl(final int width) {
        return posterSizeConfig.getImageBaseUrl(width);
    }

    // endregion

    // region Public Methods

    /**
     * Initialize. Should be call only once on application start.
     */
    @SuppressLint("CheckResult") // We won't cancel the init process so the return value can be safely ignored.
    public void init() {
        movieDbService.getConfiguration().subscribe(
                result -> updateAndSave(result.getImageConfig()),
                err -> Log.e(TAG, err.getMessage()));
    }

    // endregion

    // region Private Methods

    private void readFromConfigStore() {
        final String baseUrl = configStore.getConfigItem(IConfigStore.KEY_TMDB_IMAGE_BASE_URL);
        updateBaseUrl(baseUrl, false);

        backdropSizeConfig = backdropSizeConfigBuilder.loadFromConfigStore();
        posterSizeConfig = posterSizeConfigBuilder.loadFromConfigStore();
    }

    private void updateAndSave(final ImageConfig config) {
        if (config != null) {
            final String baseUrl = config.getBaseUrl();
            updateBaseUrl(baseUrl, true);

            final Iterable<String> backdropSizeStrings = config.getBackdropSizes();
            backdropSizeConfig = backdropSizeConfigBuilder.updateAndSave(backdropSizeConfig, backdropSizeStrings);

            final Iterable<String> posterSizeStrings = config.getPosterSizes();
            posterSizeConfig = posterSizeConfigBuilder.updateAndSave(posterSizeConfig, posterSizeStrings);
        }
    }

    /**
     * Update base URL.
     *
     * @param baseUrl new value for base URL.
     * @param save save flag. True to save to config store.
     */
    private void updateBaseUrl(final String baseUrl, final boolean save) {
        if (baseUrl != null && !baseUrl.isEmpty() && !baseUrl.equals(imageBaseUrl)) {
            imageBaseUrl = baseUrl;
            if (save) {
                configStore.saveConfigItem(IConfigStore.KEY_TMDB_IMAGE_BASE_URL, baseUrl);
            }
        }
    }

    // endregion
}