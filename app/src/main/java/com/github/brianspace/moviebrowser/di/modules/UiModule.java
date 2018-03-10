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

package com.github.brianspace.moviebrowser.di.modules;

import android.app.Activity;
import android.app.Fragment;
import com.github.brianspace.moviebrowser.di.components.FavoriteMovieListFragmentSubcomponent;
import com.github.brianspace.moviebrowser.di.components.MainActivitySubcomponent;
import com.github.brianspace.moviebrowser.di.components.MovieDetailsActivitySubcomponent;
import com.github.brianspace.moviebrowser.di.components.NowPlayingMovieListFragmentSubcomponent;
import com.github.brianspace.moviebrowser.di.components.SettingsFragmentSubcomponent;
import com.github.brianspace.moviebrowser.ui.activity.MainActivity;
import com.github.brianspace.moviebrowser.ui.activity.MovieDetailsActivity;
import com.github.brianspace.moviebrowser.ui.fragment.FavoriteMovieListFragment;
import com.github.brianspace.moviebrowser.ui.fragment.NowPlayingMovieListFragment;
import com.github.brianspace.moviebrowser.ui.fragment.SettingsFragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.android.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Dagger module for the UI classes.
 */
@Module(subcomponents = {MainActivitySubcomponent.class, MovieDetailsActivitySubcomponent.class,
        NowPlayingMovieListFragmentSubcomponent.class, FavoriteMovieListFragmentSubcomponent.class,
        SettingsFragmentSubcomponent.class})
public abstract class UiModule {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    /* default */ abstract AndroidInjector.Factory<? extends Activity> bindMainActivityInjectorFactory(
            MainActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(MovieDetailsActivity.class)
    /* default */ abstract AndroidInjector.Factory<? extends Activity> bindMovieDetailsActivityInjectorFactory(
            MovieDetailsActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(NowPlayingMovieListFragment.class)
    /* default */ abstract AndroidInjector.Factory<? extends Fragment> bindNowPlayingMovieListFragmentInjectorFactory(
            NowPlayingMovieListFragmentSubcomponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(FavoriteMovieListFragment.class)
    /* default */ abstract AndroidInjector.Factory<? extends Fragment> bindFavoriteMovieListFragmentInjectorFactory(
            FavoriteMovieListFragmentSubcomponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(SettingsFragment.class)
    /* default */ abstract AndroidInjector.Factory<? extends Fragment> bindSettingsFragmentInjectorFactory(
            SettingsFragmentSubcomponent.Builder builder);
}
