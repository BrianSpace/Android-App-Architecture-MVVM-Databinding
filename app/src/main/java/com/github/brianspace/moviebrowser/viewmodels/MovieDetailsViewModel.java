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

import android.annotation.SuppressLint;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.github.brianspace.databinding.message.IMessageSource.Type;
import com.github.brianspace.databinding.message.MessageSource;
import com.github.brianspace.moviebrowser.BR;
import com.github.brianspace.moviebrowser.R;
import com.github.brianspace.moviebrowser.models.IFavoriteMovieCollection;
import com.github.brianspace.moviebrowser.models.IImageConfig;
import com.github.brianspace.moviebrowser.models.Movie;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * View model for movie details. Used in {@link com.github.brianspace.moviebrowser.ui.activity.MovieDetailsActivity}.
 */
public class MovieDetailsViewModel extends MovieViewModel {
    // region Private Constants

    /**
     * Tag for logcat.
     */
    private static final String TAG = MovieDetailsViewModel.class.getSimpleName();

    // endregion

    // region Public Constants
    // endregion

    // region Private Inner Types
    // endregion

    // region Public Inner Types

    /**
     * The state to show in the float action button.
     */
    public enum State {
        LOADING,
        FAVORITE,
        NON_FAVORITE
    }

    // endregion

    // region Private Fields

    /**
     * Flag for loading state.
     */
    private boolean isLoading;

    /**
     * View model for similar movies.
     */
    private final MoviesViewModel similarMoviesViewModel;

    /**
     * RxJava consumer for onError callback.
     */
    private final Consumer<? super Throwable> onErrorConsumer = err -> {
        Log.e(TAG, "Failed: " + err.getLocalizedMessage());
    };

    // endregion

    // region Protected Fields
    // endregion

    // region Constructors

    /**
     * Constructor for view model layer internal usage.
     *
     * @param movie model layer movie object.
     * @param viewModelFactory interface for view model factory.
     * @param imageConfig interface for image configuration.
     * @param favoriteMovieCollection model layer collection of favorite movies.
     */
    /* default */ MovieDetailsViewModel(@NonNull final Movie movie,
            @NonNull final IViewModelFactory viewModelFactory,
            @NonNull final IImageConfig imageConfig,
            @NonNull final IFavoriteMovieCollection favoriteMovieCollection) {
        super(movie, imageConfig, favoriteMovieCollection);

        similarMoviesViewModel = new MoviesViewModel(movie.getSimilarMovies(), viewModelFactory);
    }

    // endregion

    // region Public Overrides
    // endregion

    // region Public Methods

    /**
     * Get the URL path of the movie backdrop image.
     */
    @Nullable
    public String getBackdropUrl(final int width) {
        final String backdropPath = movie.getBackdropPath();
        if (TextUtils.isEmpty(backdropPath)) {
            return null;
        }

        return imageConfig.getBackdropBaseUrl(width) + backdropPath;
    }

    /**
     * Get the movie's overview.
     */
    @Bindable
    public String getOverview() {
        return movie.getOverview();
    }

    /**
     * Load the view model.
     *
     * @return Observable result.
     */
    @NonNull
    public Completable loadDetails() {
        isLoading = true;
        return movie.loadDetails()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    isLoading = false;
                    notifyPropertyChanged(BR.tagline);
                    notifyPropertyChanged(BR.state);
                });
    }

    /**
     * Get movie state.
     */
    @Bindable
    public State getState() {
        return isLoading ? State.LOADING : isFavorite() ? State.FAVORITE : State.NON_FAVORITE;
    }

    /**
     * Get movie tag line.
     */
    @Bindable
    public String getTagline() {
        return movie.getDetails() == null ? null : movie.getDetails().getTagline();
    }

    /**
     * Get similar movies view model.
     */
    @Bindable
    public IMovieList getSimilarMovies() {
        return similarMoviesViewModel;
    }

    /**
     * Click handler for favorite button.
     */
    public void onClickFavorite() {
        if (movie.getDetails() == null) {
            return;
        }

        if (isFavorite()) {
            removeFromFavorite();
        } else {
            addToFavorite();
        }
    }

    // endregion

    // region Protected Methods
    // endregion

    // region Private Methods

    // The user may not cancel the call when leave the page so the return value can be safely ignored.
    @SuppressLint("CheckResult")
    private void addToFavorite() {
        favoriteMovieCollection.addToFavorite(movie).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    message = new MessageSource(R.string.msg_added_to_favorite, Type.NOTIFICATION);
                    notifyPropertyChanged(BR.favorite);
                    notifyPropertyChanged(BR.state);
                    notifyPropertyChanged(BR.message);
                }, onErrorConsumer);
    }

    // The user may not cancel the call when leave the page so the return value can be safely ignored.
    @SuppressLint("CheckResult")
    private void removeFromFavorite() {
        favoriteMovieCollection.removeFromFavorite(movie).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    message = new MessageSource(R.string.msg_removed_from_favorite, Type.NOTIFICATION);
                    notifyPropertyChanged(BR.favorite);
                    notifyPropertyChanged(BR.state);
                    notifyPropertyChanged(BR.message);
                }, onErrorConsumer);
    }

    // endregion
}