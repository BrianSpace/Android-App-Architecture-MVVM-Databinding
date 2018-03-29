package com.github.brianspace.moviebrowser.di.modules.ui

import com.github.brianspace.moviebrowser.ui.activity.MovieDetailsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger module for MovieDetailsActivity.
 */
@Module
abstract class MovieDetailsActivityModule {
    @ContributesAndroidInjector
    internal abstract fun contributeMovieDetailsActivityInjector(): MovieDetailsActivity
}
