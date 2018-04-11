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

package com.github.brianspace.moviebrowser.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AlertDialog.Builder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.brianspace.moviebrowser.R
import com.github.brianspace.moviebrowser.viewmodels.SettingsViewModel
import dagger.android.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_settings.*


// region Private Constants

/**
 * Tag for logcat.
 */
private const val TAG = "SettingsFragment"

// endregion

/**
 * Fragment for settings page.
 */
class SettingsFragment : DaggerFragment() {

    // region Package Private Fields

    /**
     * Data cleaner instance.
     */
    @Inject
    internal lateinit var settingsViewModel: SettingsViewModel

    /**
     * Clearing message format string.
     */
    private val msgClearingFmt by lazy {
        getString (R.string.message_clearing_fmt)
    }

    /**
     * Save subscriptions for unsubscribing during onDestroy().
     */
    private val compositeDisposable = CompositeDisposable()

    // endregion

    // region Public Overrides

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onResume() {
        super.onResume()
        clearDataBtn.setOnClickListener { _ ->
            Builder(activity).setTitle(R.string.title_confirm_clear)
                .setMessage(R.string.message_confirm_clear)
                .setPositiveButton(android.R.string.ok) { _, _ -> clearData(true) }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
        }

        clearCacheBtn.setOnClickListener { _ -> clearData(false) }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unsubscribe observers.
        compositeDisposable.dispose()
    }

    // endregion

    // region Private Methods

    private fun clearData(clearFavorites: Boolean) {
        val dialog = ProgressDialog(activity).apply {
            setTitle(R.string.title_clearing)
            setCancelable(false)
            setMessage(getString(R.string.message_clearing))
        }

        val onNextConsumer = Consumer<Int> { ret ->
            // onNext
            if (ret != 0) {
                dialog.setMessage(String.format(msgClearingFmt, getString(ret)))
            }
        }

        val onErrorConsumer = Consumer<Throwable> { // Failed
            err ->
            Log.d(TAG, "Failed!" + err?.localizedMessage)
            dialog.dismiss()
        }

        val onComplete = Action {
            // onComplete
            Log.d(TAG, "Dismiss")
            dialog.dismiss()
        }

        compositeDisposable.add(settingsViewModel.clearData(clearFavorites)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextConsumer, onErrorConsumer, onComplete))
        dialog.show()
    }
}
