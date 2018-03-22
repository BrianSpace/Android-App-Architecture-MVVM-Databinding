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
import com.github.brianspace.moviebrowser.models.DataCleaner
import com.github.brianspace.moviebrowser.models.DataCleaner.Stage
import dagger.android.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Timed
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_settings.*


// region Private Constants

/**
 * Tag for logcat.
 */
private val TAG = SettingsFragment::class.java.simpleName

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
    internal lateinit var dataCleaner: DataCleaner

    /**
     * Clearing message format string.
     */
    private val msgClearingFmt by lazy {
        getString (R.string.message_clearing_fmt)
    }

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

    // endregion

    // region Private Methods

    private fun clearData(clearFavorites: Boolean) {
        val dialog = ProgressDialog(activity).apply {
            setTitle(R.string.title_clearing)
            setCancelable(false)
            setMessage(getString(R.string.message_clearing))
        }

        val onNextConsumer = Consumer<Timed<Stage>> { ret ->
            // onNext
            Log.d(TAG, "Stage: " + ret.value())
            val msg = getStageMessage(ret.value())
            if (msg != null) {
                dialog.setMessage(msg)
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

        dataCleaner.clearData(clearFavorites)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextConsumer, onErrorConsumer, onComplete)
        dialog.show()
    }

    private fun getStageMessage(state: Stage): String? {
        var msg: String? = when (state) {
            DataCleaner.Stage.FAVORITES -> getString(R.string.message_clearing_item_favorites)
            DataCleaner.Stage.HTTP_CACHE -> getString(R.string.message_clearing_item_cache)
            DataCleaner.Stage.IMAGE_CACHE -> getString(R.string.message_clearing_item_images)
            else -> null
        }

        msg?.let {
            msg = String.format(msgClearingFmt, msg)
        }

        return msg
    }
}
