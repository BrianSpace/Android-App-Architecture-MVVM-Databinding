package com.github.brianspace.moviebrowser.di.modules

import com.github.brianspace.moviebrowser.ui.activity.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger module for MainActivity.
 */
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector
    internal abstract fun contributeMainActivityInjector(): MainActivity
}
