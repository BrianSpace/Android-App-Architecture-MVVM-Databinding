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

import android.util.Log
import com.github.brianspace.moviebrowser.repository.IConfigStore
import com.github.brianspace.moviebrowser.repository.IMovieDbService
import com.github.brianspace.moviebrowser.repository.data.Configuration.ImageConfig
import javax.inject.Inject

// region Private Constants

/**
 * Tag for logcat.
 */
private const val TAG = "TmdbConfig"

// endregion

/**
 * Configuration used in this app, from TMDb Web API.
 * For model layer internal use.
 */
internal class TmdbConfig
@Inject
constructor(
    /**
     * Interface for accessing TMDb Web API.
     */
    private val movieDbService: IMovieDbService,
    /**
     * Interface for configuration store.
     */
    private val configStore: IConfigStore
) : IImageConfig {

    // region Private Properties

    /**
     * Configuration builder for backdrop images.
     */
    private val backdropSizeConfigBuilder: ImageSizeConfigBuilder

    /**
     * Configuration builder for poster images.
     */
    private val posterSizeConfigBuilder: ImageSizeConfigBuilder

    /**
     * Base URL for images.
     */
    private var imageBaseUrl = Constants.DEFAULT_IMAGE_BASE_URL

    /**
     * Configuration for movie backdrop images. A new instance will be created when value changes.
     */
    private var backdropSizeConfig: ImageSizesConfig? = null

    /**
     * Configuration for movie poster images. A new instance will be created when value changes.
     */
    private var posterSizeConfig: ImageSizesConfig? = null

    // endregion

    // region Private Types

    /**
     * Class to help the creation of ImageSizesConfig.
     */
    private inner class ImageSizeConfigBuilder
    /**
     * Create a new instance for a specific image type.
     *
     * @param configKey Key to save & load config value from config store.
     */
    internal constructor(private val configKey: String) {

        /**
         * Load from config store.
         *
         * @return a new ImageSizesConfig instance.
         */
        internal fun loadFromConfigStore(): ImageSizesConfig {
            return ImageSizesConfig.loadFromConfigStore(imageBaseUrl, configStore, configKey)
        }

        /**
         * Update config and save the new value if needed.
         *
         * @param originalConfig the original ImageSizesConfig instance.
         * @param sizeStrings new image size strings.
         * @return a new instance of ImageSizesConfig if the new config is different from the original one, otherwise
         * the original instance is returned.
         */
        internal fun updateAndSave(
            originalConfig: ImageSizesConfig?,
            sizeStrings: Iterable<String>?
        ): ImageSizesConfig {
            val newConfig = ImageSizesConfig.createFromImageSizes(imageBaseUrl, sizeStrings!!)
            val changed = !originalConfig!!.sameAs(newConfig)
            if (changed) {
                ImageSizesConfig.saveToConfigStore(newConfig, configStore, configKey)
            }

            return if (changed) newConfig else originalConfig
        }
    }

    init {
        backdropSizeConfigBuilder = ImageSizeConfigBuilder(IConfigStore.KEY_TMDB_BACKDROP_SIZES)
        posterSizeConfigBuilder = ImageSizeConfigBuilder(IConfigStore.KEY_TMDB_POSTER_SIZES)
        readFromConfigStore()
    }

    // endregion

    // region Public Overrides

    override fun getBackdropBaseUrl(width: Int): String {
        return backdropSizeConfig!!.getImageBaseUrl(width)
    }

    override fun getPosterBaseUrl(width: Int): String {
        return posterSizeConfig!!.getImageBaseUrl(width)
    }

    // endregion

    // region Public Methods

    /**
     * Initialize. Should be call only once on application start.
     */
    fun init() {
        movieDbService.configuration.subscribe(
            { result -> updateAndSave(result.imageConfig) }
        ) { err -> Log.e(TAG, err.message) }
    }

    // endregion

    // region Private Methods

    private fun readFromConfigStore() {
        val baseUrl = configStore.getConfigItem(IConfigStore.KEY_TMDB_IMAGE_BASE_URL)
        updateBaseUrl(baseUrl, false)

        backdropSizeConfig = backdropSizeConfigBuilder.loadFromConfigStore()
        posterSizeConfig = posterSizeConfigBuilder.loadFromConfigStore()
    }

    private fun updateAndSave(config: ImageConfig?) {
        if (config != null) {
            val baseUrl = config.baseUrl
            updateBaseUrl(baseUrl, true)

            val backdropSizeStrings = config.backdropSizes
            backdropSizeConfig = backdropSizeConfigBuilder.updateAndSave(backdropSizeConfig, backdropSizeStrings)

            val posterSizeStrings = config.posterSizes
            posterSizeConfig = posterSizeConfigBuilder.updateAndSave(posterSizeConfig, posterSizeStrings)
        }
    }

    /**
     * Update base URL.
     *
     * @param baseUrl new value for base URL.
     * @param save save flag. True to save to config store.
     */
    private fun updateBaseUrl(baseUrl: String?, save: Boolean) {
        if (baseUrl != null && !baseUrl.isEmpty() && baseUrl != imageBaseUrl) {
            imageBaseUrl = baseUrl
            if (save) {
                configStore.saveConfigItem(IConfigStore.KEY_TMDB_IMAGE_BASE_URL, baseUrl)
            }
        }
    }
}
