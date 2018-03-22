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

import android.databinding.Bindable
import android.text.TextUtils
import android.util.Log
import com.github.brianspace.databinding.message.IMessageSource.Type
import com.github.brianspace.databinding.message.MessageSource
import com.github.brianspace.moviebrowser.BR
import com.github.brianspace.moviebrowser.R
import com.github.brianspace.moviebrowser.models.IFavoriteMovieCollection
import com.github.brianspace.moviebrowser.models.IImageConfig
import com.github.brianspace.moviebrowser.models.Movie
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

// region Private Constants

/**
 * Tag for logcat.
 */
private val TAG = MovieDetailsViewModel::class.java.simpleName

// endregion

/**
 * View model for movie details. Used in [com.github.brianspace.moviebrowser.ui.activity.MovieDetailsActivity].
 */
class MovieDetailsViewModel
/**
 * Constructor for view model layer internal usage.
 *
 * @param movie model layer movie object.
 * @param viewModelFactory interface for view model factory.
 * @param imageConfig interface for image configuration.
 * @param favoriteMovieCollection model layer collection of favorite movies.
 */
internal constructor(
    movie: Movie,
    viewModelFactory: IViewModelFactory,
    imageConfig: IImageConfig,
    favoriteMovieCollection: IFavoriteMovieCollection
) : MovieViewModel(movie, imageConfig, favoriteMovieCollection) {

    // region Private Properties

    /**
     * Flag for loading state.
     */
    private var isLoading: Boolean = false

    /**
     * View model for similar movies.
     */
    private val similarMoviesViewModel: MoviesViewModel = MoviesViewModel(movie.getSimilarMovies(), viewModelFactory)

    /**
     * RxJava consumer for onError callback.
     */
    private val onErrorConsumer = Consumer<Throwable> { err -> Log.e(TAG, "Failed: " + err?.localizedMessage) }

    /**
     * Get the movie's overview.
     */
    val overview: String?
        @Bindable
        get() = movie.overview

    /**
     * Get movie state.
     */
    val state: State
        @Bindable
        get() = if (isLoading) State.LOADING else if (isFavorite) State.FAVORITE else State.NON_FAVORITE

    /**
     * Get movie tag line.
     */
    val tagline: String?
        @Bindable
        get() = movie.details?.tagline

    /**
     * Get similar movies view model.
     */
    val similarMovies: IMovieList
        @Bindable
        get() = similarMoviesViewModel

    // endregion

    // region Public Inner Types

    /**
     * The state to show in the float action button.
     */
    enum class State {
        LOADING,
        FAVORITE,
        NON_FAVORITE
    }

    // endregion

    // region Public Methods

    /**
     * Get the URL path of the movie backdrop image.
     */
    fun getBackdropUrl(width: Int): String? {
        val backdropPath = movie.backdropPath
        return if (TextUtils.isEmpty(backdropPath)) {
            null
        } else imageConfig.getBackdropBaseUrl(width) + backdropPath
    }

    /**
     * Load the view model.
     *
     * @return Observable result.
     */
    fun loadDetails(): Completable {
        isLoading = true
        return movie.loadDetails()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally({
                isLoading = false
                notifyPropertyChanged(BR.tagline)
                notifyPropertyChanged(BR.state)
            })
    }

    /**
     * Click handler for favorite button.
     */
    fun onClickFavorite() {
        if (movie.details == null) {
            return
        }

        if (isFavorite) {
            removeFromFavorite()
        } else {
            addToFavorite()
        }
    }

    // endregion

    // region Protected Methods
    // endregion

    // region Private Methods

    private fun addToFavorite() {
        favoriteMovieCollection.addToFavorite(movie).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { _ ->
                message = MessageSource(R.string.msg_added_to_favorite, Type.NOTIFICATION)
                notifyPropertyChanged(BR.favorite)
                notifyPropertyChanged(BR.state)
                notifyPropertyChanged(BR.message)
            }, onErrorConsumer)
    }

    private fun removeFromFavorite() {
        favoriteMovieCollection.removeFromFavorite(movie).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { _ ->
                message = MessageSource(R.string.msg_removed_from_favorite, Type.NOTIFICATION)
                notifyPropertyChanged(BR.favorite)
                notifyPropertyChanged(BR.state)
                notifyPropertyChanged(BR.message)
            }, onErrorConsumer)
    }
}