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

package com.github.brianspace.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.brianspace.common.util.Function;
import com.github.brianspace.widgets.BuildConfig;
import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Image loader.
 *
 * @param <T> Object type providing the image URL.
 */
public class ImageLoader<T> {
    // region Private Constants

    /**
     * Tag for logcat.
     */
    private static final String TAG = ImageLoader.class.getSimpleName();

    /**
     * Options for Glide image library.
     */
    private static final RequestOptions GLIDE_OPTIONS = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.NORMAL);

    /**
     * Loading events listener instance.
     */
    private static final LoadingListener LOADING_LISTENER = new LoadingListener();

    // endregion

    // region Private Fields

    /**
     * Mapping from ImageView's hash code to the image provider.
     * Since the hash codes for ImageView objects are just memory addresses so no need to worry about hash collisions.
     */
    private final SparseArray<WeakReference<T>> viewModelMap = new SparseArray<>();

    // endregion

    // region Private Inner Types

    /**
     * OnLayoutChangeListener, used for load image after ImageView is measured so that the width is not zero.
     */
    private class OnImageViewLayoutChangeListener implements OnLayoutChangeListener {

        /**
         * Function to get image URL from movie view model.
         */
        private final Function<T, String> getUrl;

        /* default */ OnImageViewLayoutChangeListener(final Function<T, String> getUrlFunc) {
            getUrl = getUrlFunc;
        }

        @Override
        public void onLayoutChange(final View v, final int left, final int top, final int right, final int bottom,
                final int oldLeft, final int oldTop, final int oldRight, final int oldBottom) {
            if (left == right) {
                return;
            }

            final WeakReference<T> found = viewModelMap.get(v.hashCode());
            if (found == null) {
                v.removeOnLayoutChangeListener(this);
                return;
            }

            final T urlProvider = found.get();
            // Remove from map.
            viewModelMap.delete(v.hashCode());

            if (urlProvider != null) {
                final String url = getUrl.apply(urlProvider);
                loadImage((ImageView) v, url);
                v.removeOnLayoutChangeListener(this);
            }
        }
    }

    /**
     * Loading events listener for Glide.
     */
    private static class LoadingListener implements RequestListener<Drawable> {

        @Override
        public boolean onLoadFailed(@Nullable final GlideException e, final Object model, final Target<Drawable> target,
                final boolean isFirstResource) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, String.format(Locale.ROOT,
                        "GLIDE onException(%s, %s, %s, %s)", e, model, target, isFirstResource), e);
            }

            return false;
        }

        @Override
        public boolean onResourceReady(final Drawable resource, final Object model, final Target<Drawable> target,
                final DataSource dataSource, final boolean isFirstResource) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, String.format(Locale.ROOT,
                        "GLIDE onResourceReady(%s, %s, %s, %s, %s)", resource, model,
                        target, dataSource, isFirstResource));
            }

            return false;
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
    public static void loadImage(final ImageView view, final String url) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(view.getContext()).clear(view);
        } else {
            Glide.with(view.getContext())
                    .load(url)
                    .apply(GLIDE_OPTIONS)
                    .listener(LOADING_LISTENER)
                    .into(view);
        }
    }

    /**
     * Load image from the specified URL provider into the ImageView.
     *
     * @param view          the target ImageView
     * @param urlProvider   the object to provider the image URL.
     * @param getUrl        the function to get the URL of the image.
     */
    public void loadImage(final ImageView view, final T urlProvider,
            final Function<T, String> getUrl) {
        final int width = view.getWidth();
        if (width == 0) {
            // Delay the image loading till measured if the size is zero.
            viewModelMap.put(view.hashCode(), new WeakReference<T>(urlProvider));
            view.addOnLayoutChangeListener(new OnImageViewLayoutChangeListener(getUrl));
        } else {
            // Remove from map.
            viewModelMap.delete(view.hashCode());

            final String url = getUrl.apply(urlProvider);
            loadImage(view, url);
        }
    }

    // endregion
}