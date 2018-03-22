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

import java.io.File

/**
 * Utility to get various directories (using system defaults).
 */
interface IDirUtil {
    /**
     * Get available cache directory. Prefer external over internal.
     */
    val availableCacheDir: File
    /**
     * Get HTTP cache directory.
     */
    val httpCacheDir: File
}