package com.github.brianspace.moviebrowser.di.modules

import com.github.brianspace.moviebrowser.ui.fragment.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger module for SettingsFragment.
 */
@Module
abstract class SettingsFragmentModule {
    @ContributesAndroidInjector
    internal abstract fun contributeSettingsFragmentInjector(): SettingsFragment
}
