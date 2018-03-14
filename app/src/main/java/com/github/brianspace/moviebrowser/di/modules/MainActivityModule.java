package com.github.brianspace.moviebrowser.di.modules;

import com.github.brianspace.moviebrowser.ui.activity.MainActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Dagger module for MainActivity.
 */
@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector
    /* default */ abstract MainActivity contributeMainActivityInjector();
}
