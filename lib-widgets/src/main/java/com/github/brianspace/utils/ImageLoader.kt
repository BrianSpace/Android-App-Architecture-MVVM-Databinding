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

package com.github.brianspace.utils

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.brianspace.widgets.BuildConfig
import java.lang.ref.WeakReference
import java.util.Locale

// region Private Constants

/**
 * Tag for logcat.
 */
private const val TAG = "ImageLoader"

// endregion

// region Private Properties

/**
 * Options for Glide image library.
 */
private val requestOptions = RequestOptions()
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .priority(Priority.NORMAL)

/**
 * Loading events listener instance.
 */
private val loadingListener = LoadingListener()

// endregion

// region Private Types

/**
 * Loading events listener for Glide.
 */
private class LoadingListener : RequestListener<Drawable> {

    override fun onLoadFailed(
        e: GlideException?, model: Any, target: Target<Drawable>,
        isFirstResource: Boolean
    ): Boolean {
        if (BuildConfig.DEBUG) {
            Log.e(
                TAG, String.format(
                    Locale.ROOT,
                    "GLIDE onException(%s, %s, %s, %s)", e, model, target, isFirstResource
                )
            )
        }

        return false
    }

    override fun onResourceReady(
        resource: Drawable, model: Any, target: Target<Drawable>,
        dataSource: DataSource, isFirstResource: Boolean
    ): Boolean {
        if (BuildConfig.DEBUG) {
            Log.v(
                TAG, String.format(
                    Locale.ROOT,
                    "GLIDE onResourceReady(%s, %s, %s, %s, %s)", resource, model,
                    target, dataSource, isFirstResource
                )
            )
        }

        return false
    }
}

// endregion

// region Public Methods

/**
 * Load image from the specified URL into the ImageView.
 *
 * @param view  the target ImageView
 * @param url   the URL of the image to be loaded
 */
fun loadImage(view: ImageView, url: String?) {
    if (TextUtils.isEmpty(url)) {
        Glide.with(view.context).clear(view)
    } else {
        Glide.with(view.context)
            .load(url)
            .apply(requestOptions)
            .listener(loadingListener)
            .into(view)
    }
}

// endregion

/**
 * Image loader.
 *
 * @param <T> Object type providing the image URL.
 */
class ImageLoader<T> {

    // region Private Properties

    /**
     * Mapping from ImageView's hash code to the image provider.
     * Since the hash codes for ImageView objects are just memory addresses so no need to worry about hash collisions.
     */
    private val viewModelMap = SparseArray<WeakReference<T>>()

    // endregion

    // region Private Inner Types

    /**
     * OnLayoutChangeListener, used for load image after ImageView is measured so that the width is not zero.
     */
    private inner class OnImageViewLayoutChangeListener internal constructor(
        /**
         * Function to get image URL from movie view model.
         */
        private val getUrl: (T) -> String?
    ) : OnLayoutChangeListener {

        override fun onLayoutChange(
            v: View, left: Int, top: Int, right: Int, bottom: Int,
            oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
        ) {
            if (left == right) {
                return
            }

            val found = viewModelMap.get(v.hashCode())
            if (found == null) {
                v.removeOnLayoutChangeListener(this)
                return
            }

            val urlProvider = found.get()
            // Remove from map.
            viewModelMap.delete(v.hashCode())

            if (urlProvider != null) {
                val url = getUrl(urlProvider)
                loadImage(v as ImageView, url)
                v.removeOnLayoutChangeListener(this)
            }
        }
    }

    // endregion

    // region Public Methods

    /**
     * Load image from the specified URL provider into the ImageView, after the size is known.
     *
     * @param view          the target ImageView
     * @param urlProvider   the object to provider the image URL.
     * @param getUrl        the function to get the URL of the image.
     */
    fun loadImage(view: ImageView, urlProvider: T, getUrl: (T) -> String?) {
        val width = view.width
        if (width == 0) {
            // Delay the image loading till measured if the size is zero.
            viewModelMap.put(view.hashCode(), WeakReference(urlProvider))
            view.addOnLayoutChangeListener(OnImageViewLayoutChangeListener(getUrl))
        } else {
            // Remove from map.
            viewModelMap.delete(view.hashCode())

            val url = getUrl(urlProvider)
            loadImage(view, url)
        }
    }

    // endregion
}
