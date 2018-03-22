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

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import com.github.brianspace.common.observable.IObserver;
import com.github.brianspace.databinding.message.IMessageSource;
import com.github.brianspace.databinding.message.MessageSource;
import com.github.brianspace.moviebrowser.BR;
import com.github.brianspace.moviebrowser.models.IFavoriteMovieCollection;
import com.github.brianspace.moviebrowser.models.IImageConfig;
import com.github.brianspace.moviebrowser.models.Movie;
import com.github.brianspace.moviebrowser.ui.nav.NavigationHelper;

/**
 * View model for movie item.
 */
@SuppressWarnings("PMD.CommentSize")
public class MovieViewModel extends BaseObservable {

    // region Protected/Private Fields

    /**
     * Model layer movie object.
     */
    /* default */ final Movie movie;

    /**
     * Interface for image configuration.
     */
    protected final IImageConfig imageConfig;

    /**
     * Model layer collection of favorite movies.
     */
    protected final IFavoriteMovieCollection favoriteMovieCollection;

    /**
     * Message source for displaying notifications.
     */
    protected MessageSource message;

    /**
     * Handler of the main looper.
     */
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Change observer of the underlying movie model, to propagate the change to UI.
     * - Currently only favorite state will change.
     * - A field is required to keep the reference from being collected.
     */
    private final IObserver itemObserver = (observable, data) ->
            mainHandler.post(() -> notifyPropertyChanged(BR.favorite));

    // endregion

    // region Constructors

    /**
     * Constructor for view model layer internal usage.
     *
     * @param movie model layer movie object.
     * @param imageConfig interface for image configuration.
     * @param favoriteMovieCollection model layer collection of favorite movies.
     */
    /* default */ MovieViewModel(@NonNull final Movie movie, @NonNull final IImageConfig imageConfig,
            @NonNull final IFavoriteMovieCollection favoriteMovieCollection) {
        this.movie = movie;
        this.imageConfig = imageConfig;
        this.favoriteMovieCollection = favoriteMovieCollection;

        movie.addObserver(itemObserver);
    }

    // endregion

    // region Public Methods

    /**
     * Get the movie ID.
     */
    public int getId() {
        return movie.getId();
    }

    /**
     * Get the URL of the movie poster image.
     */
    @Nullable
    public String getPosterUrl(final int width) {
        final String posterPath = movie.getPosterPath();
        if (TextUtils.isEmpty(posterPath)) {
            return null;
        }

        return imageConfig.getPosterBaseUrl(width) + posterPath;
    }

    /**
     * Get the movie's title.
     */
    @Bindable
    public String getTitle() {
        return movie.getTitle();
    }

    /**
     * Get the movie's rating (0~5).
     */
    @Bindable
    public float getRating() {
        return movie.getVoteAverage() / 2;
    }

    /**
     * Get if the movie is favorite or not.
     */
    @Bindable
    public boolean isFavorite() {
        return movie.isFavorite();
    }

    /**
     * Get notification message.
     */
    @Bindable
    public IMessageSource getMessage() {
        return message;
    }

    /**
     * Click handler for the movie item.
     */
    public void onClickItem(final View view) {
        NavigationHelper.navigateToMovieDetails(view.getContext(), movie.getId());
    }

    // endregion

    // region Package Private Methods

    /**
     * Check if the movie has the specified ID or not.
     *
     * @param id movie ID to check.
     * @return true if the ID is the same as input, otherwise false.
     */
    /* default */ boolean hasId(final int id) {
        return movie.getId() == id;
    }

    // endregion
}
