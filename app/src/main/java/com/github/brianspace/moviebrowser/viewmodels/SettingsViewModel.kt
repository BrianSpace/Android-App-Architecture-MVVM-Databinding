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

package com.github.brianspace.moviebrowser.viewmodels

import android.util.Log
import com.github.brianspace.moviebrowser.R
import com.github.brianspace.moviebrowser.models.DataCleaner
import dagger.Lazy
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

// region Private Constants

/**
 * Tag for logcat.
 */
private const val TAG = "SettingsViewModel"

// endregion

/**
 * View model for settings fragment.
 */
@Singleton
class SettingsViewModel
/**
 * Constructor for SettingsViewModel.
 * @param dataCleaner the lazy injection handler for DataCleaner.
 */
@Inject
internal constructor(
    private val dataCleaner: Lazy<DataCleaner>
) {
    // region Public Methods

    /**
     * Clear application data.
     *
     * @param clearFavorites true to clear favorite movies, otherwise or not.
     * @return  RxJava Observable for the string resource ID. 0 if no message to show.
     */
    fun clearData(clearFavorites: Boolean): Observable<Int> {
        return dataCleaner.get().clearData(clearFavorites)
            .map { stageTimed ->
                Log.d(TAG, "Stage: " + stageTimed.value())
                when (stageTimed.value()) {
                    DataCleaner.Stage.FAVORITES -> R.string.message_clearing_item_favorites
                    DataCleaner.Stage.HTTP_CACHE -> R.string.message_clearing_item_cache
                    DataCleaner.Stage.IMAGE_CACHE -> R.string.message_clearing_item_images
                    else -> 0
                }
            }
    }

    // endregion
}
