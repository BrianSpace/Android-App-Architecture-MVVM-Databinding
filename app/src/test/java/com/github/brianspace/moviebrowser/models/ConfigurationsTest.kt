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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

import com.github.brianspace.moviebrowser.repository.IConfigStore
import com.github.brianspace.moviebrowser.repository.IMovieDbService
import java.util.Arrays
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


// region Private Constants

private val IMAGE_SIZE_STRINGS = arrayOf("w92", "w154", "w185", "w342", "w500", "w780", "original")
private val IMAGE_SIZES = intArrayOf(92, 154, 185, 342, 500, 780, Integer.MAX_VALUE)
private val SIZE_STRINGS = Arrays.asList(*IMAGE_SIZE_STRINGS)
private const val DEFAULT_BASE_URL = Constants.DEFAULT_IMAGE_BASE_URL + "original/"

// endregion

/**
 * Test for TMDb configuration models.
 */
@RunWith(MockitoJUnitRunner::class)
class ConfigurationsTest {

    // region Private Properties

    @Mock
    private lateinit var configStore: IConfigStore

    /**
     * Returns Mockito.eq() as nullable type to avoid java.lang.IllegalStateException when
     * null is returned.
     *
     * Generic T is nullable because implicitly bounded by Any?.
     *
     * Reference: https://stackoverflow.com/questions/30305217/is-it-possible-to-use-mockito-in-kotlin/35776132
     */
    private fun <T> eq(obj: T): T = Mockito.eq<T>(obj)

    /**
     * Returns Mockito.any() as nullable type to avoid java.lang.IllegalStateException when
     * null is returned.
     */
    private fun <T> any(): T = Mockito.any<T>()

    // endregion

    // region Public Methods

    @Test
    @Throws(Exception::class)
    fun testImageSizesConfigCreation() {
        val config1 = ImageSizesConfig.createFromImageSizes(
            Constants.DEFAULT_IMAGE_BASE_URL, SIZE_STRINGS
        )

        `when`<String>(configStore.getConfigItem(IConfigStore.KEY_TMDB_POSTER_SIZES))
            .thenReturn(ImageSizesConfig.getConcatenatedString(SIZE_STRINGS))
        val config2 = ImageSizesConfig.loadFromConfigStore(
            Constants.DEFAULT_IMAGE_BASE_URL, configStore, IConfigStore.KEY_TMDB_POSTER_SIZES
        )

        verify(configStore).getConfigItem(eq(IConfigStore.KEY_TMDB_POSTER_SIZES))
        assertTrue("Two configs should have the same value.", config1.sameAs(config2))
    }

    @Test
    @Throws(Exception::class)
    fun testDefaultImageSizesConfigCreation() {
        `when`<String>(configStore.getConfigItem(IConfigStore.KEY_TMDB_POSTER_SIZES)).thenReturn(null)
        val config = ImageSizesConfig.loadFromConfigStore(
            Constants.DEFAULT_IMAGE_BASE_URL, configStore, IConfigStore.KEY_TMDB_POSTER_SIZES
        )

        verify(configStore).getConfigItem(eq(IConfigStore.KEY_TMDB_POSTER_SIZES))
        assertEquals("Default image base URL not match.", DEFAULT_BASE_URL, config.getImageBaseUrl(1))
    }


    @Test
    @Throws(Exception::class)
    fun testImageSizesConfigSaving() {
        val config = ImageSizesConfig.createFromImageSizes(
            Constants.DEFAULT_IMAGE_BASE_URL, SIZE_STRINGS
        )

        ImageSizesConfig.saveToConfigStore(config, configStore, IConfigStore.KEY_TMDB_POSTER_SIZES)

        verify(configStore).saveConfigItem(
            eq(IConfigStore.KEY_TMDB_POSTER_SIZES),
            eq(ImageSizesConfig.getConcatenatedString(SIZE_STRINGS))
        )
    }

    @Test
    fun testTmdbConfig() {
        `when`<String>(configStore.getConfigItem(IConfigStore.KEY_TMDB_IMAGE_BASE_URL))
            .thenReturn(Constants.DEFAULT_IMAGE_BASE_URL)
        `when`<String>(configStore.getConfigItem(IConfigStore.KEY_TMDB_POSTER_SIZES))
            .thenReturn(ImageSizesConfig.getConcatenatedString(SIZE_STRINGS))
        `when`<String>(configStore.getConfigItem(IConfigStore.KEY_TMDB_BACKDROP_SIZES))
            .thenReturn(ImageSizesConfig.getConcatenatedString(SIZE_STRINGS))

        val service = mock(IMovieDbService::class.java)
        val config = TmdbConfig(service, configStore)

        verify(configStore).getConfigItem(eq(IConfigStore.KEY_TMDB_IMAGE_BASE_URL))
        verify(configStore).getConfigItem(eq(IConfigStore.KEY_TMDB_POSTER_SIZES))
        verify(configStore).getConfigItem(eq(IConfigStore.KEY_TMDB_BACKDROP_SIZES))

        for (i in IMAGE_SIZES.indices) {
            val width = IMAGE_SIZES[i]

            assertEquals(
                "Image width on boundary should use boundary value!",
                Constants.DEFAULT_IMAGE_BASE_URL + IMAGE_SIZE_STRINGS[i] + '/'.toString(),
                config.getPosterBaseUrl(width)
            )

            if (i > 1) {
                assertEquals(
                    "Image size less than boundary but larger than last boundary should use boundary value!",
                    Constants.DEFAULT_IMAGE_BASE_URL + IMAGE_SIZE_STRINGS[i] + '/'.toString(),
                    config.getPosterBaseUrl(width - 1)
                )
            }

            if (i < IMAGE_SIZES.size - 1) {
                assertEquals(
                    "Image width larger than boundary should use next boundary value!",
                    Constants.DEFAULT_IMAGE_BASE_URL + IMAGE_SIZE_STRINGS[i + 1] + '/'.toString(),
                    config.getPosterBaseUrl(width + 1)
                )
            }
        }
    }
}
