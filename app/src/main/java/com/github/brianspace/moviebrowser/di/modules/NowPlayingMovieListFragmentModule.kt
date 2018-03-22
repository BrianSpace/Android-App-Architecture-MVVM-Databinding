package com.github.brianspace.moviebrowser.di.modules

import com.github.brianspace.moviebrowser.ui.fragment.NowPlayingMovieListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger module for NowPlayingMovieListFragment.
 */
@Module
abstract class NowPlayingMovieListFragmentModule {
    @ContributesAndroidInjector
    internal abstract fun contributeNowPlayingMovieListFragmentInjector(): NowPlayingMovieListFragment
}
