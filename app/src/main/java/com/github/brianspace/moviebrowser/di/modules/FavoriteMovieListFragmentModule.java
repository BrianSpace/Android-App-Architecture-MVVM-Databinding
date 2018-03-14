package com.github.brianspace.moviebrowser.di.modules;

import com.github.brianspace.moviebrowser.ui.fragment.FavoriteMovieListFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Dagger module for MainActivity.
 */
@Module
public abstract class FavoriteMovieListFragmentModule {
    @ContributesAndroidInjector
    /* default */ abstract FavoriteMovieListFragment contributeFavoriteMovieListFragmentInjector();
}
