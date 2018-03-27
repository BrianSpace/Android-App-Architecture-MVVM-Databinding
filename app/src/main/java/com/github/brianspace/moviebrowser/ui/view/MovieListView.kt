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

package com.github.brianspace.moviebrowser.ui.view

import android.content.Context
import android.databinding.ObservableList
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.github.brianspace.databinding.adapter.RecyclerViewDatabindingAdapter
import com.github.brianspace.moviebrowser.BR
import com.github.brianspace.moviebrowser.R
import com.github.brianspace.moviebrowser.viewmodels.IMovieList
import com.github.brianspace.moviebrowser.viewmodels.MovieViewModel
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_movie_list.view.*

// region Private Constants

/**
 * Tag for logcat.
 */
private const val TAG = "MovieListView"

// endregion

/**
 * Movie list view, supports pulling to refresh or load more.
 */
class MovieListView : FrameLayout {

    // region Private Fields

    /**
     * Movie list view model.
     */
    private var movieList: IMovieList? = null

    /**
     * State for refreshing.
     */
    private var isRefreshing: Boolean = false

    /**
     * State for loading next page.
     */
    private var isLoadingNextPage: Boolean = false

    /**
     * RxJava Observer for loading events.
     */
    private val loadingObserver = LoadingObserver()

    // endregion

    // region Private Inner Types

    /**
     * RxJava Observer for loading.
     */
    private inner class LoadingObserver : CompletableObserver {
        override fun onSubscribe(d: Disposable) {
            // Not used.
        }

        override fun onError(e: Throwable) {
            Log.w(TAG, "LoadingObserver.onError: " + e.toString())
            swipeRefreshLayout.isRefreshing = false
            stopLoadingAnimation()
            Toast.makeText(context, R.string.error_fetch_movie_list, Toast.LENGTH_LONG).show()
        }

        override fun onComplete() {
            swipeRefreshLayout.isRefreshing = false
            swipeRefreshLayout.direction = if (movieList?.hasNexPage() == true) {
                SwipyRefreshLayoutDirection.BOTH
            } else {
                SwipyRefreshLayoutDirection.TOP
            }
            stopLoadingAnimation()
            isRefreshing = false
            isLoadingNextPage = false
        }
    }

    /**
     * RecyclerView adapter for movie view model list.
     *
     * @param itemList Observable item list.
     */
    private class MoviesAdapter
    internal constructor(itemList: ObservableList<MovieViewModel>) :
        RecyclerViewDatabindingAdapter<MovieViewModel>(itemList, BR.movie, R.layout.item_poster) {

        init {
            setHasStableIds(true)
        }

        override fun getItemId(position: Int): Long {
            return adapterItems[position].id.toLong()
        }
    }

    // endregion

    // region Constructors

    /**
     * Constructor.
     * @param context Context.
     */
    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    /**
     * Constructor.
     * @param context Context.
     * @param attrs Attributes.
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    /**
     * Constructor.
     * @param context Context.
     * @param attrs Attributes.
     * @param defStyleAttr Style attribute.
     */
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    // endregion

    // region Public Methods

    /**
     * Set movie list.
     * @param movieList Movie list. Set to null to clear the bindings and references.
     */
    fun setMovieList(movieList: IMovieList?) {
        this.movieList = movieList

        if (movieList == null) {
            swipeRefreshLayout.setOnRefreshListener(null)
            movieGridView.adapter = null
        } else {
            movieGridView.adapter = MoviesAdapter(movieList.movies)

            swipeRefreshLayout.direction = if (movieList.hasNexPage())
                SwipyRefreshLayoutDirection.BOTH
            else
                SwipyRefreshLayoutDirection.TOP

            if (!movieList.isLoaded) {
                startLoadingAnimation()
                movieList.load().subscribe(loadingObserver)
            }
        }
    }

    // endregion

    // region Private Methods

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        View.inflate(context, R.layout.view_movie_list, this)
        initWidgets()
    }

    private fun initWidgets() {
        swipeRefreshLayout.direction = SwipyRefreshLayoutDirection.TOP

        swipeRefreshLayout.setOnRefreshListener { direction ->
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                if (!isRefreshing && movieList != null) {
                    movieList?.refresh()?.subscribe(loadingObserver)
                }
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM && !isLoadingNextPage && movieList != null) {
                movieList?.loadNextPage()?.subscribe(loadingObserver)
            }
        }
    }

    private fun startLoadingAnimation() {
        loadingProgressBar.visibility = View.VISIBLE
        loadingProgressBar.isIndeterminate = true
    }

    private fun stopLoadingAnimation() {
        loadingProgressBar.visibility = View.GONE
    }

    // endregion
}
