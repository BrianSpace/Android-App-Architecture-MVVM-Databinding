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

package com.github.brianspace.moviebrowser.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.brianspace.moviebrowser.R;
import com.github.brianspace.moviebrowser.models.DataCleaner;
import com.github.brianspace.moviebrowser.models.DataCleaner.Stage;
import dagger.android.DaggerFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Timed;
import javax.inject.Inject;

/**
 * Fragment for settings page.
 */
public class SettingsFragment extends DaggerFragment {

    // region Private Constants

    /**
     * Tag for logcat.
     */
    private static final String TAG = SettingsFragment.class.getSimpleName();

    // endregion

    // region Package Private Fields

    /**
     * Data cleaner instance.
     */
    @Inject
    /* default */ DataCleaner dataCleaner;

    // endregion

    // region Public Overrides

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            final Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
        root.findViewById(R.id.clear_data_btn).setOnClickListener(view -> {
            new Builder(getActivity()).setTitle(R.string.title_confirm_clear)
                    .setMessage(R.string.message_confirm_clear)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> clearData(true))
                    .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                    })
                    .show();
        });
        root.findViewById(R.id.clear_cache_btn).setOnClickListener(view -> clearData(false));
        return root;
    }

    // endregion

    // region Private Methods

    private void clearData(final boolean clearFavorites) {
        final AlertDialog dialog = new ProgressDialog.Builder(getActivity())
                .setTitle(R.string.title_clearing)
                .setMessage(R.string.message_clearing)
                .setCancelable(false).create();

        final Consumer<Timed<Stage>> onNextConsumer = ret -> {
            // onNext
            Log.d(TAG, "Stage: " + ret.value());
            final String msg = getStageMessage(ret.value());
            if (msg != null) {
                dialog.setMessage(msg);
            }
        };

        final Consumer<Throwable> onErrorConsumer = err -> { // Failed
            Log.d(TAG, "Failed!" + err.getLocalizedMessage());
            dialog.dismiss();
        };

        final Action onComplete = () -> { // onComplete
            Log.d(TAG, "Dismiss");
            dialog.dismiss();
        };

        dataCleaner.clearData(clearFavorites)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextConsumer, onErrorConsumer, onComplete);
        dialog.show();
    }

    @Nullable
    private String getStageMessage(final Stage state) {
        final String msgClearingFmt = getString(R.string.message_clearing_fmt);
        String msg;
        switch (state) {
            case FAVORITES:
                msg = getString(R.string.message_clearing_item_favorites);
                break;
            case HTTP_CACHE:
                msg = getString(R.string.message_clearing_item_cache);
                break;
            case IMAGE_CACHE:
                msg = getString(R.string.message_clearing_item_images);
                break;
            default:
                msg = null;
                break;
        }

        if (msg != null) {
            msg = String.format(msgClearingFmt, msg);
        }

        return msg;
    }

    // endregion
}
