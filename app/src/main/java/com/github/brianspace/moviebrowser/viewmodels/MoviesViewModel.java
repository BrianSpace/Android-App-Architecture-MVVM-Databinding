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
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import com.github.brianspace.common.observable.ICollectionObserver;
import com.github.brianspace.common.observable.IObservable;
import com.github.brianspace.moviebrowser.BR;
import com.github.brianspace.moviebrowser.models.IMovieCollection;
import com.github.brianspace.moviebrowser.models.Movie;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

/**
 * View model for movie list, with support for paging.
 */
class MoviesViewModel extends BaseObservable implements IMovieList {

    // region Protected Fields

    /**
     * Interface for accessing model level movie collection.
     */
    @SuppressWarnings("WeakerAccess")
    protected final IMovieCollection movieCollection;

    /**
     * Interface for view model factory.
     */
    @SuppressWarnings("WeakerAccess")
    protected final IViewModelFactory viewModelFactory;

    // endregion

    // region Private Fields

    /**
     * Loading state.
     */
    private boolean isLoading;

    /**
     * List of movie view models.
     */
    private final ObservableArrayList<MovieViewModel> movies = new ObservableArrayList<>();

    /**
     * Observer instance for the model layer list of movies.
     */
    private final MovieListObserver movieListObserver = new MovieListObserver();

    // endregion

    // region Private Inner Types

    /**
     * Observer for the model layer list of movies.
     */
    private class MovieListObserver implements ICollectionObserver {

        @SuppressWarnings({"PMD.NcssCount", "PMD.CyclomaticComplexity"}) // Simple switch cases.
        @Override
        public void onUpdate(final IObservable<ICollectionObserver> observable, final Action action, final Object item,
                final List<?> range) {
            switch (action) {
                case Clear:
                    if (!movies.isEmpty()) {
                        movies.clear();
                    }
                    break;
                case AppendItem:
                    if (item != null) {
                        final Movie movie = (Movie) item;
                        movies.add(viewModelFactory.createMovieViewModel(movie));
                    }
                    break;
                case AppendRange:
                    if (range != null && !range.isEmpty()) {
                        final List<MovieViewModel> appendList = new ArrayList<>();
                        for (final Object addedItem : range) {
                            appendList.add(0, viewModelFactory.createMovieViewModel((Movie) addedItem));
                        }

                        movies.addAll(appendList);
                    }
                    break;
                case AddItemToFront:
                    if (item != null) {
                        final Movie movie = (Movie) item;
                        movies.add(0, viewModelFactory.createMovieViewModel(movie));
                    }
                    break;
                case RemoveItem:
                    int indexToRemove = -1;
                    final Movie movie = (Movie) item;
                    for (int index = 0; index < movies.size(); ++index) {
                        if (movies.get(index).hasId(movie.getId())) {
                            indexToRemove = index;
                            break;
                        }
                    }

                    if (indexToRemove >= 0) {
                        movies.remove(indexToRemove);
                    }
                    break;
                case UpdateItem:
                    // Not used for now.
                    break;
                default:
                    break;
            }
        }
    }

    // endregion

    // region Constructors

    /**
     * Constructor for view model layer internal usage.
     *
     * @param movieCollection interface for accessing model layer movies.
     * @param viewModelFactory interface for view model object store.
     */
    /* default */ MoviesViewModel(@NonNull final IMovieCollection movieCollection,
            @NonNull final IViewModelFactory viewModelFactory) {
        this.movieCollection = movieCollection;
        this.viewModelFactory = viewModelFactory;

        movieCollection.addObserver(movieListObserver);
    }

    // endregion

    // region Public Overrides

    @Override
    public boolean isLoaded() {
        return movies.size() > 0;
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Bindable
    @NonNull
    @Override
    public final ObservableList<MovieViewModel> getMovies() {
        return movies;
    }

    @Override
    @NonNull
    public final Completable load() {
        isLoading = true;
        if (movieCollection.isLoaded()) {
            final List<MovieViewModel> appendList = new ArrayList<>();
            for (final Movie movie : movieCollection.getMovies()) {
                appendList.add(viewModelFactory.createMovieViewModel(movie));
            }

            movies.addAll(appendList);
            isLoading = false;
            notifyPropertyChanged(BR.movies);
            return Completable.complete();
        }

        return movieCollection.load()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> isLoading = false);
    }

    @Override
    @NonNull
    public final Completable refresh() {
        isLoading = true;
        movies.clear();
        return movieCollection.refresh()
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> isLoading = false);
    }

    @Override
    public boolean hasNexPage() {
        return movieCollection.hasNexPage();
    }

    @Override
    @NonNull
    public final Completable loadNextPage() {
        isLoading = true;
        return movieCollection.loadNextPage()
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> isLoading = false);
    }

    // endregion
}
