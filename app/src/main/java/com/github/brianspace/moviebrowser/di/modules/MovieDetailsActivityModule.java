package com.github.brianspace.moviebrowser.di.modules;

import com.github.brianspace.moviebrowser.ui.activity.MovieDetailsActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Dagger module for MovieDetailsActivity.
 */
@Module
public abstract class MovieDetailsActivityModule {
    @ContributesAndroidInjector
    /* default */ abstract MovieDetailsActivity contributeMovieDetailsActivityInjector();
}
