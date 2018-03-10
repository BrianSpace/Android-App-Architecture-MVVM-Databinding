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

package com.github.brianspace.moviebrowser.repository.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.github.brianspace.moviebrowser.di.qualifiers.ApplicationContext;
import com.github.brianspace.moviebrowser.repository.IConfigStore;
import java.util.Set;
import javax.inject.Inject;

/**
 * Configuration storage.
 */
class ConfigStore implements IConfigStore {
    // region Private Constants

    /**
     * Key for SharedPreferences.
     */
    private static final String PREF_KEY_CONFIG = "PREF_KEY_CONFIG";

    // endregion

    // region Private Fields

    /**
     * SharedPreferences to save and store configs.
     */
    private final SharedPreferences sharedPreferences;

    // endregion

    // region Constructors

    @Inject
    /* default */ ConfigStore(@NonNull @ApplicationContext final Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_KEY_CONFIG, Context.MODE_PRIVATE);
    }

    // endregion

    // region Public Overrides

    @Override
    @Nullable
    public String getConfigItem(@NonNull final String key) {
        return sharedPreferences.getString(key, null);
    }

    @Override
    public void saveConfigItem(@NonNull final String key, @NonNull final String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    @Nullable
    @Override
    public Set<String> getConfigSet(@NonNull final String key) {
        return sharedPreferences.getStringSet(key, null);
    }

    @Override
    public void saveConfigSet(@NonNull final String key, @NonNull final Set<String> values) {
        sharedPreferences.edit().putStringSet(key, values).apply();
    }

    // endregion
}