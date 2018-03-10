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

package com.github.brianspace.moviebrowser.repository.data;

import java.util.List;

/**
 * System wide configuration information for TMDb API.
 * @see <a href="https://developers.themoviedb.org/3/configuration/get-api-configuration">Configuration</a>
 */
@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class Configuration {
    private ImageConfig images;
    private List<String> changeKeys;

    /**
     * Configuration for images.
     * To build an image URL, you will need 3 pieces of data. The base_url, size and file_path.
     * Simply combine them all and you will have a fully qualified URL. Here’s an example URL:
     *      https://image.tmdb.org/t/p/w500/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg
     */
    public static class ImageConfig {
        private String baseUrl;
        private String secureBaseUrl;
        private List<String> backdropSizes;
        private List<String> logoSizes;
        private List<String> posterSizes;
        private List<String> profileSizes;
        private List<String> stillSizes;

        /**
         * Get the image base URL, which is used to construct the full URL for an image.
         */
        public String getBaseUrl() {
            return baseUrl;
        }

        /**
         * Get the HTTPS image base URL.
         */
        public String getSecureBaseUrl() {
            return secureBaseUrl;
        }

        /**
         * Get the supported sizes for the movie backdrop images. Example:
         *  [w300, w780, w1280, original]
         */
        public final Iterable<String> getBackdropSizes() {
            return backdropSizes;
        }

        /**
         * Get the supported sizes for the logo images. Example:
         *  [w45, w92, w154, w185, w300, w500, original]
         */
        public final Iterable<String> getLogoSizes() {
            return logoSizes;
        }

        /**
         * Get the supported sizes for the poster images. Example:
         *  [w92, w154, w185, w342, w500, w780, original]
         */
        public final Iterable<String> getPosterSizes() {
            return posterSizes;
        }

        /**
         * Get the supported sizes for the profile images. Example:
         *  [w45, w185, h632, original]
         */
        public final Iterable<String> getProfileSizes() {
            return profileSizes;
        }

        /**
         * Get the supported sizes for the still images. Example:
         *  [w92, w185, w300, original]
         */
        public final Iterable<String> getStillSizes() {
            return stillSizes;
        }
    }

    /**
     * Get the configuration for images.
     */
    public ImageConfig getImageConfig() {
        return images;
    }

    /**
     * Get the list of keys for change feed from TMDb.
     */
    public final Iterable<String> getChangeKeys() {
        return changeKeys;
    }
}
