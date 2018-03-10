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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.brianspace.moviebrowser.repository.IConfigStore;
import com.github.brianspace.moviebrowser.repository.IMovieDbService;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test for TMDb configuration models.
 */
@SuppressWarnings({"PMD.CommentRequired"})
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationsTest {
    // region Private Constants

    private static final String[] IMAGE_SIZE_STRINGS = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};
    private static final int[] IMAGE_SIZES = {92, 154, 185, 342, 500, 780, Integer.MAX_VALUE};
    private static final List<String> SIZE_STRINGS = Arrays.asList(IMAGE_SIZE_STRINGS);

    private static final String DEFAULT_BASE_URL = Constants.DEFAULT_IMAGE_BASE_URL + "original/";

    @Mock
    /* default */ IConfigStore configStore;

    // endregion

    // region Public Methods

    @Test
    public void testImageSizesConfigCreation() throws Exception {
        final ImageSizesConfig config1 = ImageSizesConfig.createFromImageSizes(
                Constants.DEFAULT_IMAGE_BASE_URL, SIZE_STRINGS);

        when(configStore.getConfigItem(IConfigStore.KEY_TMDB_POSTER_SIZES))
                .thenReturn(ImageSizesConfig.getConcatenatedString(SIZE_STRINGS));
        final ImageSizesConfig config2 = ImageSizesConfig.loadFromConfigStore(
                Constants.DEFAULT_IMAGE_BASE_URL, configStore, IConfigStore.KEY_TMDB_POSTER_SIZES);

        verify(configStore).getConfigItem(eq(IConfigStore.KEY_TMDB_POSTER_SIZES));
        assertTrue("Two configs should have the same value.", config1.sameAs(config2));
    }

    @Test
    public void testDefaultImageSizesConfigCreation() throws Exception {
        when(configStore.getConfigItem(IConfigStore.KEY_TMDB_POSTER_SIZES)).thenReturn(null);
        final ImageSizesConfig config = ImageSizesConfig.loadFromConfigStore(
                Constants.DEFAULT_IMAGE_BASE_URL, configStore, IConfigStore.KEY_TMDB_POSTER_SIZES);

        verify(configStore).getConfigItem(eq(IConfigStore.KEY_TMDB_POSTER_SIZES));
        assertEquals("Default image base URL not match.", DEFAULT_BASE_URL, config.getImageBaseUrl(1));
    }


    @Test
    public void testImageSizesConfigSaving() throws Exception {
        final ImageSizesConfig config = ImageSizesConfig.createFromImageSizes(
                Constants.DEFAULT_IMAGE_BASE_URL, SIZE_STRINGS);

        ImageSizesConfig.saveToConfigStore(config, configStore, IConfigStore.KEY_TMDB_POSTER_SIZES);

        verify(configStore).saveConfigItem(eq(IConfigStore.KEY_TMDB_POSTER_SIZES),
                eq(ImageSizesConfig.getConcatenatedString(SIZE_STRINGS)));
    }

    @Test
    public void testTmdbConfig() {
        when(configStore.getConfigItem(IConfigStore.KEY_TMDB_IMAGE_BASE_URL))
                .thenReturn(Constants.DEFAULT_IMAGE_BASE_URL);
        when(configStore.getConfigItem(IConfigStore.KEY_TMDB_POSTER_SIZES))
                .thenReturn(ImageSizesConfig.getConcatenatedString(SIZE_STRINGS));
        when(configStore.getConfigItem(IConfigStore.KEY_TMDB_BACKDROP_SIZES))
                .thenReturn(ImageSizesConfig.getConcatenatedString(SIZE_STRINGS));

        final IMovieDbService service = mock(IMovieDbService.class);
        final TmdbConfig config = new TmdbConfig(service, configStore);

        verify(configStore).getConfigItem(eq(IConfigStore.KEY_TMDB_IMAGE_BASE_URL));
        verify(configStore).getConfigItem(eq(IConfigStore.KEY_TMDB_POSTER_SIZES));
        verify(configStore).getConfigItem(eq(IConfigStore.KEY_TMDB_BACKDROP_SIZES));

        for (int i = 0; i < IMAGE_SIZES.length; ++i) {
            final int width = IMAGE_SIZES[i];

            assertEquals("Image width on boundary should use boundary value!",
                    Constants.DEFAULT_IMAGE_BASE_URL + IMAGE_SIZE_STRINGS[i] + '/',
                    config.getPosterBaseUrl(width));

            if (i > 1) {
                assertEquals("Image size less than boundary but larger than last boundary should use boundary value!",
                        Constants.DEFAULT_IMAGE_BASE_URL + IMAGE_SIZE_STRINGS[i] + '/',
                        config.getPosterBaseUrl(width - 1));
            }

            if (i < IMAGE_SIZES.length - 1) {
                assertEquals("Image width larger than boundary should use next boundary value!",
                        Constants.DEFAULT_IMAGE_BASE_URL + IMAGE_SIZE_STRINGS[i + 1] + '/',
                        config.getPosterBaseUrl(width + 1));
            }
        }
    }

    // endregion

    // region Private Methods
    // endregion
}