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

package com.github.brianspace.moviebrowser.ui.databinding;

import android.databinding.BindingAdapter;
import android.databinding.ObservableList;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.github.brianspace.moviebrowser.R;
import com.github.brianspace.moviebrowser.viewmodels.MovieDetailsViewModel;
import com.github.brianspace.moviebrowser.viewmodels.MovieViewModel;
import com.github.brianspace.utils.ImageLoader;
import com.github.brianspace.utils.ImageLoaderKt;

/**
 * Data binding adapters (for custom attribute setters used in layout files).
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class DataBindingAdapter {

    // region Private Constants

    /**
     * Tag for logcat.
     */
    private static final String TAG = DataBindingAdapter.class.getSimpleName();

    // endregion

    // region Private Fields

    /**
     * Image loader for MovieViewModel.
     */
    private static final ImageLoader<MovieViewModel> MOVIE_IMAGE_LOADER = new ImageLoader<>();

    /**
     * Image loader for MovieDetailsViewModel.
     */
    private static final ImageLoader<MovieDetailsViewModel> MOVIE_DETAILS_IMAGE_LOADER = new ImageLoader<>();

    // endregion

    // region Constructors

    private DataBindingAdapter() throws InstantiationException {
        throw new InstantiationException("Utility class DataBindingAdapter should not be instantiated!");
    }

    // endregion

    // region Public Methods

    /**
     * Binding "imageUrl" to load an image.
     *
     * @param view the ImageView
     * @param url the URL of the image to be loaded
     */
    @BindingAdapter({"imageUrl"})
    public static void loadImage(final ImageView view, final String url) {
        ImageLoaderKt.loadImage(view, url);
    }

    /**
     * Binding "moviePoster" to load the poster image for a movie.
     *
     * @param view the ImageView
     * @param movie the view model of the movie whose poster image is to be loaded.
     */
    @BindingAdapter({"moviePoster"})
    public static void loadPosterImage(final ImageView view, final MovieViewModel movie) {
        MOVIE_IMAGE_LOADER.loadImage(view, movie, (m) -> m.getPosterUrl(view.getWidth()));
    }

    /**
     * Binding "movieBackdrop" to load the backdrop image for a movie.
     *
     * @param view the ImageView
     * @param movie the view model of the movie whose backdrop image is to be loaded.
     */
    @BindingAdapter({"movieBackdrop"})
    public static void loadBackdropImage(final ImageView view, final MovieDetailsViewModel movie) {
        MOVIE_DETAILS_IMAGE_LOADER.loadImage(view, movie, (m) -> m.getBackdropUrl(view.getWidth()));
    }

    /**
     * Binding "visibility" to the presence of the movie view models.
     *
     * @param view the View to set visibility
     * @param movies movies view model
     */
    @BindingAdapter({"visibility"})
    public static void setVisibilityByListSize(final View view, final ObservableList<MovieViewModel> movies) {
        if (movies.size() > 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * Binding "visibility" to the a boolean value.
     *
     * @param view the View to set visibility
     * @param value the boolean value to bind. True for VISIBLE and False for GONE.
     */
    @BindingAdapter({"visibility"})
    public static void setVisibilityByBoolean(final View view, final boolean value) {
        if (value) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * Binding FAB's icon to the "state".
     *
     * @param fab the FAB
     * @param state state
     */
    @BindingAdapter({"state"})
    public static void setFavoriteState(final FloatingActionButton fab, final MovieDetailsViewModel.State state) {
        if (state == null) {
            return;
        }

        switch (state) {
            case LOADING:
                changeToLoadingState(fab);
                break;
            case NON_FAVORITE:
                fab.clearAnimation();
                fab.setImageResource(R.drawable.ic_favorite_border);
                break;
            case FAVORITE:
                fab.clearAnimation();
                fab.setImageResource(R.drawable.ic_favorite);
                break;
            default:
                break;
        }
    }

    // endregion

    // region Private Methods

    private static void changeToLoadingState(final FloatingActionButton fab) {
        fab.setImageResource(R.drawable.ic_progress);
        final Animation rotateAnimation = AnimationUtils.loadAnimation(fab.getContext(), R.anim.rotate);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        fab.startAnimation(rotateAnimation);
    }

    // endregion
}
