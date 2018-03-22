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

package com.github.brianspace.moviebrowser.ui.view;

import android.content.Context;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.brianspace.databinding.adapter.RecyclerViewDatabindingAdapter;
import com.github.brianspace.moviebrowser.BR;
import com.github.brianspace.moviebrowser.R;
import com.github.brianspace.moviebrowser.viewmodels.IMovieList;
import com.github.brianspace.moviebrowser.viewmodels.MovieViewModel;
import com.github.brianspace.widgets.DynamicGridView;
import com.github.brianspace.widgets.SwipeRefreshLayoutEx;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

/**
 * Movie list view, supports pulling to refresh or load more.
 */
public class MovieListView extends FrameLayout {

    // region Private Constants

    /**
     * Tag for logcat.
     */
    private static final String TAG = MovieListView.class.getSimpleName();

    // endregion

    // region Package Private Fields

    /**
     * The SwipeRefreshLayoutEx control.
     */
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.swipe_refresh)
    /* default */ SwipeRefreshLayoutEx swipeRefreshLayout;

    /**
     * The DynamicGridView to show the movies.
     */
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.movie_list)
    /* default */ DynamicGridView movieGridView;

    /**
     * ProgressBar for data loading.
     */
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.loading)
    /* default */ ProgressBar loadingProgressBar;

    // endregion

    // region Private Fields

    /**
     * Movie list view model.
     */
    private IMovieList movieList;

    /**
     * State for refreshing.
     */
    private boolean isRefreshing;

    /**
     * State for loading next page.
     */
    private boolean isLoadingNextPage;

    /**
     * RxJava Observer for loading events.
     */
    private final LoadingObserver loadingObserver = new LoadingObserver();

    // endregion

    // region Private Inner Types

    /**
     * RxJava Observer for loading.
     */
    private class LoadingObserver implements CompletableObserver {
        @Override
        public void onSubscribe(final Disposable d) {
            // Not used.
        }

        @Override
        public void onError(final Throwable e) {
            Log.w(TAG, "LoadingObserver.onError: " + e.toString());
            swipeRefreshLayout.setRefreshing(false);
            stopLoadingAnimation();
            Toast.makeText(getContext(), R.string.error_fetch_movie_list, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onComplete() {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setDirection(movieList != null && movieList.hasNexPage()
                    ? SwipyRefreshLayoutDirection.BOTH : SwipyRefreshLayoutDirection.TOP);
            stopLoadingAnimation();
            isRefreshing = false;
            isLoadingNextPage = false;
        }
    }

    /**
     * RecyclerView adapter for movie view model list.
     */
    private static class MoviesAdapter extends RecyclerViewDatabindingAdapter<MovieViewModel> {

        /**
         * Constructor for MoviesAdapter.
         *
         * @param itemList Observable item list.
         */
        /* default */ MoviesAdapter(@NonNull final ObservableList<MovieViewModel> itemList) {
            super(itemList, BR.movie, R.layout.item_poster);
            setHasStableIds(true);
        }

        @Override
        public long getItemId(final int position) {
            return adapterItems.get(position).getId();
        }
    }

    // endregion

    // region Constructors

    /**
     * Constructor.
     * @param context Context.
     */
    public MovieListView(final Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * Constructor.
     * @param context Context.
     * @param attrs Attributes.
     */
    public MovieListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * Constructor.
     * @param context Context.
     * @param attrs Attributes.
     * @param defStyleAttr Style attribute.
     */
    public MovieListView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    // endregion

    // region Public Methods

    /**
     * Set movie list.
     * @param movieList Movie list. Set to null to clear the bindings and references.
     */
    public void setMovieList(@Nullable final IMovieList movieList) {
        this.movieList = movieList;

        if (movieList == null) {
            swipeRefreshLayout.setOnRefreshListener(null);
            movieGridView.setAdapter(null);
        } else {
            final MoviesAdapter adapter = new MoviesAdapter(movieList.getMovies());
            movieGridView.setAdapter(adapter);

            swipeRefreshLayout.setDirection(
                    movieList.hasNexPage() ? SwipyRefreshLayoutDirection.BOTH
                            : SwipyRefreshLayoutDirection.TOP);

            if (!movieList.isLoaded()) {
                startLoadingAnimation();
                movieList.load().subscribe(loadingObserver);
            }
        }
    }

    // endregion

    // region Private Methods

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private void init(final AttributeSet attrs, final int defStyleAttr) {
        View.inflate(getContext(), R.layout.view_movie_list, this);
        initWidgets();
    }

    private void initWidgets() {
        ButterKnife.bind(this);

        swipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);

        swipeRefreshLayout.setOnRefreshListener(direction -> {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                if (!isRefreshing && movieList != null) {
                    movieList.refresh().subscribe(loadingObserver);
                }
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM && !isLoadingNextPage && movieList != null) {
                movieList.loadNextPage().subscribe(loadingObserver);
            }
        });
    }

    private void startLoadingAnimation() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        loadingProgressBar.setIndeterminate(true);
    }

    private void stopLoadingAnimation() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    // endregion
}
