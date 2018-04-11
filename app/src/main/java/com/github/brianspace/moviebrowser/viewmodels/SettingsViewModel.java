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

package com.github.brianspace.moviebrowser.viewmodels;

import android.util.Log;
import com.github.brianspace.moviebrowser.R;
import com.github.brianspace.moviebrowser.models.DataCleaner;
import dagger.Lazy;
import io.reactivex.Observable;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * View model for settings fragment.
 */
@Singleton
public class SettingsViewModel {

    // region Private Constants

    /**
     * Tag for logcat.
     */
    private static final String TAG = SettingsViewModel.class.getSimpleName();

    // endregion

    // region Package Private Fields

    /**
     * Data cleaner instance.
     */
    private final Lazy<DataCleaner> dataCleaner;

    // endregion

    // region Constructors

    /**
     * Constructor for SettingsViewModel.
     * @param dataCleaner the lazy injection handler for DataCleaner.
     */
    @Inject
    /* default */ SettingsViewModel(final Lazy<DataCleaner> dataCleaner) {
        this.dataCleaner = dataCleaner;
    }

    // endregion

    // region Public Methods

    /**
     * Clear application data.
     *
     * @param clearFavorites true to clear favorite movies, otherwise or not.
     * @return  RxJava Observable for the string resource ID. 0 if no message to show.
     */
    public Observable<Integer> clearData(final boolean clearFavorites) {
        return dataCleaner.get().clearData(clearFavorites)
                .map(stageTimed -> {
                    Log.d(TAG, "Stage: " + stageTimed.value());
                    switch (stageTimed.value()) {
                        case FAVORITES:
                            return R.string.message_clearing_item_favorites;
                        case HTTP_CACHE:
                            return R.string.message_clearing_item_cache;
                        case IMAGE_CACHE:
                            return R.string.message_clearing_item_images;
                        default:
                            break;
                    }

                    return 0;
                });
    }

    // endregion
}
