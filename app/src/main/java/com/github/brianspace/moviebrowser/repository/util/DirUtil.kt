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

package com.github.brianspace.moviebrowser.repository.util

import android.content.Context
import com.github.brianspace.moviebrowser.di.qualifiers.ApplicationContext

import java.io.File

// region Private Constants

/**
 * Directory name for HTTP cache.
 */
private const val HTTP_CACHE_DIR_NAME = "responses"

// endregion

/**
 * Directory utilities.
 */
class DirUtil
/**
 * @param appContext Application context.
 */
internal constructor(@param:ApplicationContext private val appContext: Context) : IDirUtil {
    // region Private Fields

    /**
     * The [File][java.io.File] instance for the cache directory.
     */
    private val cacheDir: File by lazy {
        appContext.externalCacheDir ?: appContext.cacheDir
    }

    // endregion

    // region Public Methods

    /**
     * Get available cache directory. Prefer external over internal.
     */
    override val availableCacheDir: File
        get() = cacheDir

    /**
     * Get HTTP cache directory.
     */
    override val httpCacheDir: File
        get() = File(cacheDir, HTTP_CACHE_DIR_NAME)

    // endregion
}
