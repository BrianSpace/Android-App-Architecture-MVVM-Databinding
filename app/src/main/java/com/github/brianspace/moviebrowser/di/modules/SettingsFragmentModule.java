package com.github.brianspace.moviebrowser.di.modules;

import com.github.brianspace.moviebrowser.ui.fragment.SettingsFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Dagger module for SettingsFragment.
 */
@Module
public abstract class SettingsFragmentModule {
    @ContributesAndroidInjector
    /* default */ abstract SettingsFragment contributeSettingsFragmentInjector();
}
