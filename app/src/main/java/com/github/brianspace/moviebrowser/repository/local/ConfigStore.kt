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

package com.github.brianspace.moviebrowser.repository.local

import android.content.Context
import com.github.brianspace.moviebrowser.di.qualifiers.ApplicationContext
import com.github.brianspace.moviebrowser.repository.IConfigStore
import javax.inject.Inject

// region Private Constants

/**
 * Key for SharedPreferences.
 */
private const val PREF_KEY_CONFIG = "PREF_KEY_CONFIG"

// endregion

/**
 * Configuration storage.
 */
internal class ConfigStore
@Inject
constructor(@ApplicationContext context: Context) : IConfigStore {

    // region Private Properties

    /**
     * SharedPreferences to save and store configs.
     */
    private val sharedPreferences = context.getSharedPreferences(PREF_KEY_CONFIG, Context.MODE_PRIVATE)

    // endregion

    // region Public Overrides

    override fun getConfigItem(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun saveConfigItem(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getConfigSet(key: String): Set<String>? {
        return sharedPreferences.getStringSet(key, null)
    }

    override fun saveConfigSet(key: String, values: Set<String>) {
        sharedPreferences.edit().putStringSet(key, values).apply()
    }

    // endregion
}
