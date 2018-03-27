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

package com.github.brianspace.moviebrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.brianspace.moviebrowser.ui.view.MovieListView
import com.github.brianspace.moviebrowser.viewmodels.IMovieList
import dagger.android.DaggerFragment

/**
 * Fragment for movie list.
 * Create subclass and override getMovieList.
 */
abstract class MovieListFragment : DaggerFragment() {

    /**
     * The view created to show the movie list.
     */
    private var movieListView: MovieListView? = null

    override fun onDestroyView() {
        super.onDestroyView()
        // Release the reference to view model to prevent leak of the fragment.
        movieListView?.setMovieList(null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        movieListView = MovieListView(this.activity)
        movieListView!!.setMovieList(getMovieList())
        return movieListView
    }

    /**
     * Subclasses should override this to return the movie list view model.
     */
    protected abstract fun getMovieList() : IMovieList
}
