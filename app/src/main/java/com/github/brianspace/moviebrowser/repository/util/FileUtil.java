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

package com.github.brianspace.moviebrowser.repository.util;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * File utilities.
 */
public final class FileUtil {

    // region Private Constants

    /**
     * Directory name for HTTP cache.
     */
    private static final String HTTP_CACHE_DIR_NAME = "responses";

    // endregion

    // region Private Fields

    /**
     * Saved application context.
     */
    private static Context appContext;

    /**
     * The {@link java.io.File File} instance for the HTTP cache directory.
     */
    private static File cacheDir;

    // endregion

    // region Constructors

    private FileUtil() throws InstantiationException {
        throw new InstantiationException("Utility class FileUtil should not be instantiated!");
    }

    // endregion

    // region Public Methods

    /**
     * Initialize.
     * @param context Context.
     */
    public static void init(final Context context) {
        appContext = context.getApplicationContext();
    }

    /**
     * Get available cache directory. Prefer external over internal.
     */
    @NonNull
    public static File getAvailableCacheDir() {
        if (cacheDir != null) {
            return cacheDir;
        }

        final File externalCacheDir = appContext.getExternalCacheDir();
        cacheDir = externalCacheDir == null ? appContext.getCacheDir() : externalCacheDir;
        return cacheDir;
    }

    /**
     * Get HTTP cache directory.
     */
    @NonNull
    public static File getHttpCacheDir() {
        return new File(getAvailableCacheDir(), HTTP_CACHE_DIR_NAME);
    }

    // endregion
}
