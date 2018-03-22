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

package com.github.brianspace.moviebrowser.ui.activity

import android.databinding.DataBindingUtil
import android.databinding.ObservableList
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.github.brianspace.databinding.adapter.HeaderedRecyclerViewDatabindingAdapter
import com.github.brianspace.moviebrowser.BR
import com.github.brianspace.moviebrowser.R
import com.github.brianspace.moviebrowser.databinding.ActivityMovieDetailsBinding
import com.github.brianspace.moviebrowser.ui.nav.PATH_DETAILS
import com.github.brianspace.moviebrowser.ui.nav.QUERY_ID
import com.github.brianspace.moviebrowser.ui.nav.getValidNavUri
import com.github.brianspace.moviebrowser.viewmodels.IViewModelFactory
import com.github.brianspace.moviebrowser.viewmodels.MovieDetailsViewModel
import com.github.brianspace.moviebrowser.viewmodels.MovieViewModel
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import kotlinx.android.synthetic.main.activity_movie_details.*
import javax.inject.Inject


// region Private Constants

/**
 * Tag for logcat.
 */
private val TAG = MovieDetailsActivity::class.java.simpleName

// endregion

/**
 * Activity used to show movie details.
 */
class MovieDetailsActivity : DaggerAppCompatActivity() {

    // endregion

    // region Private Fields

    /**
     * View model object store.
     */
    @Inject
    internal lateinit var viewModelFactory: IViewModelFactory

    /**
     * Data binding for the current activity.
     */
    private var binding: ActivityMovieDetailsBinding? = null

    /**
     * Save subscriptions for unsubscribing during onDestroy().
     */
    private val compositeDisposable = CompositeDisposable()

    // endregion

    // region Private Inner Types

    /**
     * RecyclerView adapter for similar movies.
     */
    private class SimilarMoviesAdapter
    /**
     * Constructor for SimilarMoviesAdapter.
     *
     * @param itemList Observable item list.
     * @param headerParams Parameters for the list header.
     */
    internal constructor(
        itemList: ObservableList<MovieViewModel>,
        headerParams: HeaderedRecyclerViewDatabindingAdapter.HeaderParams
    ) : HeaderedRecyclerViewDatabindingAdapter<MovieViewModel>(itemList, BR.movie, R.layout.item_poster, headerParams) {

        init {
            setHasStableIds(true)
        }

        override fun getItemId(position: Int): Long {
            // Use 0 as the fixed ID for the header item.
            return (if (position == 0) 0 else adapterItems[position - 1].id).toLong()
        }
    }

    // endregion

    // region Public Overrides

    override// More items may be added later.
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // endregion

    // region Protected Overrides

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details)
        setSupportActionBar(toolbar)

        // Set backdrop image height.
        val layoutParams = appBar.layoutParams
        layoutParams.height = (resources.displayMetrics.widthPixels * backdropImage.aspectRatio).toInt()
        appBar.layoutParams = layoutParams

        handleIntent()

        // Enable "Back" button in the tool bar.
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Release the reference to view model to prevent leak of MovieDetailsActivity.
        swipeRefresh.setOnRefreshListener(null)
        similarMovieList.adapter = null
        binding = null

        // Unsubscribe observers.
        compositeDisposable.dispose()
    }

    // endregion

    // region Private Methods

    private fun handleIntent() {
        val navUri = getValidNavUri()
        if (navUri.pathNoLeadingSlash == null || !navUri.pathNoLeadingSlash!!.startsWith(PATH_DETAILS)) {
            return
        }

        val idStr = navUri.uri!!.getQueryParameter(QUERY_ID)
        if (TextUtils.isEmpty(idStr)) {
            return
        }

        try {
            val id = Integer.parseInt(idStr)
            if (id <= 0) {
                return
            }

            showMovieDetails(id)
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Invalid ID in intent: $idStr")
        }

    }

    private fun showMovieDetails(id: Int) {
        val movie = viewModelFactory.createMovieDetailsViewModelById(id) ?: return

        binding?.movie = movie
        compositeDisposable.add(
            movie.loadDetails().subscribe(Functions.EMPTY_ACTION,
                Consumer { err ->
                    // onError
                    Log.w(TAG, "showMovieDetails onError: " + err.toString())
                    Toast.makeText(this, R.string.error_fetch_movie_details, LENGTH_LONG).show()
                })
        )

        loadSimilarMovies(movie)
    }

    private fun loadSimilarMovies(movie: MovieDetailsViewModel) {
        val similarMovies = movie.similarMovies
        val headerParams = HeaderedRecyclerViewDatabindingAdapter.HeaderParams(
            R.layout.view_movie_details_header,
            BR.movie, movie
        )
        similarMovieList.adapter = SimilarMoviesAdapter(similarMovies.movies, headerParams)

        val onComplete = Action { swipeRefresh.isRefreshing = false }
        val onError = Consumer<Throwable> { err ->
            Log.w(TAG, "showMovieDetails onError: " + err.toString())
            swipeRefresh.isRefreshing = false
            Toast.makeText(this, R.string.error_fetch_similar_movies, LENGTH_LONG).show()
        }

        compositeDisposable.add(similarMovies.load().subscribe(onComplete, onError))

        swipeRefresh.direction = SwipyRefreshLayoutDirection.BOTTOM
        swipeRefresh.setDistanceToTriggerSync(
            resources.getDimensionPixelSize(com.github.brianspace.widgets.R.dimen.default_trigger_distance)
        )
        swipeRefresh.setOnRefreshListener { direction ->
            if (direction == SwipyRefreshLayoutDirection.BOTTOM && !similarMovies.isLoading) {
                compositeDisposable.add(similarMovies.loadNextPage().subscribe(onComplete, onError))
            }
        }
    }
}