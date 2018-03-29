package com.github.brianspace.moviebrowser.di.modules.ui

import com.github.brianspace.moviebrowser.ui.fragment.FavoriteMovieListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger module for MainActivity.
 */
@Module
abstract class FavoriteMovieListFragmentModule {
    @ContributesAndroidInjector
    internal abstract fun contributeFavoriteMovieListFragmentInjector(): FavoriteMovieListFragment
}
