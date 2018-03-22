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

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import com.github.brianspace.common.observable.IObservable
import com.github.brianspace.common.observable.IObserver
import com.github.brianspace.databinding.message.MessageSource
import com.github.brianspace.moviebrowser.BR
import com.github.brianspace.moviebrowser.models.IFavoriteMovieCollection
import com.github.brianspace.moviebrowser.models.IImageConfig
import com.github.brianspace.moviebrowser.models.Movie
import com.github.brianspace.moviebrowser.ui.nav.navigateToMovieDetails

/**
 * View model for movie item.
 */
open class MovieViewModel
internal constructor(
    /**
     * Model layer movie object.
     */
    protected val movie: Movie,
    /**
     * Interface for image configuration.
     */
    protected val imageConfig: IImageConfig,
    /**
     * Model layer collection of favorite movies.
     */
    protected val favoriteMovieCollection: IFavoriteMovieCollection
) : BaseObservable() {

    // region Protected/Private Properties

    /**
     * Handler of the main looper.
     */
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Change observer of the underlying movie model, to propagate the change to UI.
     * - Currently only favorite state will change.
     * - A field is required to keep the reference from being collected.
     */
    private val itemObserver = object : IObserver {
        override fun onUpdate(observable: IObservable<IObserver>, data: Any?) {
            mainHandler.post { notifyPropertyChanged(BR.favorite) }
        }
    }

    // endregion

    // region Public Properties

    /**
     * Get the movie ID.
     */
    val id: Int
        get() = movie.id

    /**
     * Message source for displaying notifications.
     */
    var message: MessageSource? = null
        @Bindable
        get

    /**
     * Get the movie's title.
     */
    val title: String
        @Bindable
        get() = movie.title

    /**
     * Get the movie's rating (0~5).
     */
    val rating: Float
        @Bindable
        get() = movie.voteAverage / 2

    /**
     * Get if the movie is favorite or not.
     */
    val isFavorite: Boolean
        @Bindable
        get() = movie.isFavorite

    init {
        movie.addObserver(itemObserver)
    }

    // endregion

    // region Public Methods

    /**
     * Get the URL of the movie poster image.
     */
    fun getPosterUrl(width: Int): String? {
        val posterPath = movie.posterPath
        return if (TextUtils.isEmpty(posterPath)) {
            null
        } else imageConfig.getPosterBaseUrl(width) + posterPath!!

    }

    /**
     * Click handler for the movie item.
     */
    fun onClickItem(view: View) {
        view.context.navigateToMovieDetails(movie.id)
    }

    // endregion

    // region Package Private Methods

    /**
     * Check if the movie has the specified ID or not.
     *
     * @param id movie ID to check.
     * @return true if the ID is the same as input, otherwise false.
     */
    internal fun hasId(id: Int): Boolean {
        return movie.id == id
    }

    // endregion
}
