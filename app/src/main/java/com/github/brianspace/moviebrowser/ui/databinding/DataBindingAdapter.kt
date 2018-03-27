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

package com.github.brianspace.moviebrowser.ui.databinding

import android.databinding.BindingAdapter
import android.databinding.ObservableList
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.github.brianspace.moviebrowser.R
import com.github.brianspace.moviebrowser.viewmodels.MovieDetailsViewModel
import com.github.brianspace.moviebrowser.viewmodels.MovieViewModel
import com.github.brianspace.utils.ImageLoader

/**
 * Data binding adapters (for custom attribute setters used in layout files).
 */

// region Private Properties

/**
 * Image loader for MovieViewModel.
 */
private val movieImageLoader = ImageLoader<MovieViewModel>()

/**
 * Image loader for MovieDetailsViewModel.
 */
private val movieDetailsImageLoader = ImageLoader<MovieDetailsViewModel>()

// endregion

// region Public Methods

/**
 * Binding "imageUrl" to load an image.
 *
 * @param view the ImageView
 * @param url the URL of the image to be loaded
 */
@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String) {
    loadImage(view, url)
}

/**
 * Binding "moviePoster" to load the poster image for a movie.
 *
 * @param view the ImageView
 * @param movie the view model of the movie whose poster image is to be loaded.
 */
@BindingAdapter("moviePoster")
fun loadPosterImage(view: ImageView, movie: MovieViewModel) {
    movieImageLoader.loadImage(view, movie, { m -> m.getPosterUrl(view.width) })
}

/**
 * Binding "movieBackdrop" to load the backdrop image for a movie.
 *
 * @param view the ImageView
 * @param movie the view model of the movie whose backdrop image is to be loaded.
 */
@BindingAdapter("movieBackdrop")
fun loadBackdropImage(view: ImageView, movie: MovieDetailsViewModel) {
    movieDetailsImageLoader.loadImage(view, movie, { m -> m.getBackdropUrl(view.width) })
}

/**
 * Binding "visibility" to the presence of the movie view models.
 *
 * @param view the View to set visibility
 * @param movies movies view model
 */
@BindingAdapter("visibility")
fun setVisibilityByListSize(view: View, movies: ObservableList<MovieViewModel>) {
    if (movies.size > 0) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

/**
 * Binding "visibility" to the a boolean value.
 *
 * @param view the View to set visibility
 * @param value the boolean value to bind. True for VISIBLE and False for GONE.
 */
@BindingAdapter("visibility")
fun setVisibilityByBoolean(view: View, value: Boolean) {
    if (value) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

/**
 * Binding FAB's icon to the "state".
 *
 * @param fab the FAB
 * @param state state
 */
@BindingAdapter("state")
fun setFavoriteState(fab: FloatingActionButton, state: MovieDetailsViewModel.State?) {
    if (state == null) {
        return
    }

    when (state) {
        MovieDetailsViewModel.State.LOADING -> changeToLoadingState(fab)
        MovieDetailsViewModel.State.NON_FAVORITE -> {
            fab.clearAnimation()
            fab.setImageResource(R.drawable.ic_favorite_border)
        }
        MovieDetailsViewModel.State.FAVORITE -> {
            fab.clearAnimation()
            fab.setImageResource(R.drawable.ic_favorite)
        }
    }
}

// endregion

// region Private Methods

private fun changeToLoadingState(fab: FloatingActionButton) {
    fab.setImageResource(R.drawable.ic_progress)
    val rotateAnimation = AnimationUtils.loadAnimation(fab.context, R.anim.rotate).apply {
        repeatMode = Animation.RESTART
        repeatCount = Animation.INFINITE
    }

    fab.startAnimation(rotateAnimation)
}

// endregion
