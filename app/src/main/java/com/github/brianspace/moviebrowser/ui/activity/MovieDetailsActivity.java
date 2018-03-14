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

package com.github.brianspace.moviebrowser.ui.activity;

import static android.widget.Toast.LENGTH_LONG;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;
import com.github.brianspace.databinding.adapter.HeaderedRecyclerViewDatabindingAdapter;
import com.github.brianspace.moviebrowser.BR;
import com.github.brianspace.moviebrowser.R;
import com.github.brianspace.moviebrowser.databinding.ActivityMovieDetailsBinding;
import com.github.brianspace.moviebrowser.ui.nav.NavigationHelper;
import com.github.brianspace.moviebrowser.viewmodels.IMovieList;
import com.github.brianspace.moviebrowser.viewmodels.IViewModelFactory;
import com.github.brianspace.moviebrowser.viewmodels.MovieDetailsViewModel;
import com.github.brianspace.moviebrowser.viewmodels.MovieViewModel;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.Functions;
import javax.inject.Inject;

/**
 * Activity used to show movie details.
 */
public class MovieDetailsActivity extends DaggerAppCompatActivity {

    // region Private Constants

    /**
     * Tag for logcat.
     */
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    // endregion

    // region Private Fields

    /**
     * View model object store.
     */
    @Inject
    /* default */ IViewModelFactory viewModelFactory;

    /**
     * Data binding for the current activity.
     */
    private ActivityMovieDetailsBinding binding;

    // endregion

    // region Private Inner Types

    /**
     * RxJava Observer for similar movies.
     */
    private static class SimilarMoviesObserver implements CompletableObserver {

        /**
         * The SwipyRefreshLayout control, used to stop its animation when an error occurs.
         */
        private final SwipyRefreshLayout swipeRefresh;

        /* default */ SimilarMoviesObserver(@NonNull final SwipyRefreshLayout swipeRefresh) {
            this.swipeRefresh = swipeRefresh;
        }

        @Override
        public void onSubscribe(final Disposable d) {
            // Not used
        }

        @Override
        public void onError(final Throwable e) {
            swipeRefresh.setRefreshing(false);
            Toast.makeText(swipeRefresh.getContext(), R.string.error_fetch_similar_movies, LENGTH_LONG).show();
        }

        @Override
        public void onComplete() {
            swipeRefresh.setRefreshing(false);
        }
    }

    // endregion

    // region Public Overrides

    @SuppressWarnings("PMD.TooFewBranchesForASwitchStatement") // More items may be added later.
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // endregion

    // region Protected Overrides

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        setSupportActionBar(binding.toolbar);

        // Set backdrop image height.
        final ViewGroup.LayoutParams layoutParams = binding.appBar.getLayoutParams();
        layoutParams.height = (int) (getResources().getDisplayMetrics().widthPixels
                * binding.backdropImage.getAspectRatio());
        binding.appBar.setLayoutParams(layoutParams);

        handleIntent();

        // Enable "Back" button in the tool bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the reference to view model to prevent leak of MovieDetailsActivity.
        binding.swipeRefresh.setOnRefreshListener(null);
        binding.similarMovieList.setAdapter(null);
        binding = null;
    }

    // endregion

    // region Private Methods

    private void handleIntent() {
        final NavigationHelper.NavUriParts navUri = NavigationHelper.getValidNavUri(this);
        if (navUri.pathNoLeadingSlash == null
                || !navUri.pathNoLeadingSlash.startsWith(NavigationHelper.PATH_DETAILS)) {
            return;
        }

        final String idStr = navUri.uri.getQueryParameter(NavigationHelper.QUERY_ID);
        if (TextUtils.isEmpty(idStr)) {
            return;
        }

        try {
            final int id = Integer.parseInt(idStr);
            if (id <= 0) {
                return;
            }

            showMovieDetails(id);
        } catch (final NumberFormatException e) {
            Log.e(TAG, "Invalid ID in intent: " + idStr);
        }
    }

    private void showMovieDetails(final int id) {
        final MovieDetailsViewModel movie = viewModelFactory.createMovieDetailsViewModelById(id);
        if (movie == null) {
            return;
        }

        binding.setMovie(movie);
        movie.loadDetails().subscribe(Functions.EMPTY_ACTION,
                err -> { // onError
                    Toast.makeText(this, R.string.error_fetch_movie_details, LENGTH_LONG).show();
                });

        loadSimilarMovies(movie);
    }

    private void loadSimilarMovies(final MovieDetailsViewModel movie) {
        final IMovieList similarMovies = movie.getSimilarMovies();
        final HeaderedRecyclerViewDatabindingAdapter.HeaderParams headerParams =
                new HeaderedRecyclerViewDatabindingAdapter.HeaderParams(R.layout.view_movie_details_header,
                        BR.movie, movie);
        final HeaderedRecyclerViewDatabindingAdapter<MovieViewModel> similarMoviesAdapter =
                new HeaderedRecyclerViewDatabindingAdapter<>(similarMovies.getMovies(), BR.movie,
                        R.layout.item_poster, headerParams);
        binding.similarMovieList.setAdapter(similarMoviesAdapter);
        similarMovies.load().subscribe(new SimilarMoviesObserver(binding.swipeRefresh));

        binding.swipeRefresh.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        binding.swipeRefresh.setDistanceToTriggerSync(
                getResources().getDimensionPixelSize(com.github.brianspace.widgets.R.dimen.default_trigger_distance));
        binding.swipeRefresh.setOnRefreshListener(direction -> {
            if (direction == SwipyRefreshLayoutDirection.BOTTOM && !similarMovies.isLoading()) {
                similarMovies.loadNextPage().subscribe(
                        new SimilarMoviesObserver(binding.swipeRefresh)
                );
            }
        });
    }

    // endregion
}