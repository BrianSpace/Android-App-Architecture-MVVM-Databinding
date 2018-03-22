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

package com.github.brianspace.moviebrowser.di.components

import com.github.brianspace.moviebrowser.MovieBrowserApplication
import com.github.brianspace.moviebrowser.di.modules.AppModule
import com.github.brianspace.moviebrowser.di.modules.FavoriteMovieListFragmentModule
import com.github.brianspace.moviebrowser.di.modules.MainActivityModule
import com.github.brianspace.moviebrowser.di.modules.MovieDetailsActivityModule
import com.github.brianspace.moviebrowser.di.modules.NowPlayingMovieListFragmentModule
import com.github.brianspace.moviebrowser.di.modules.SettingsFragmentModule
import com.github.brianspace.moviebrowser.models.ModelsModule
import com.github.brianspace.moviebrowser.repository.local.LocalRepositoryModule
import com.github.brianspace.moviebrowser.repository.web.MovieDbServiceModule
import com.github.brianspace.moviebrowser.viewmodels.ViewModelsModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Dagger component for the application.
 * Note: AndroidSupportInjectionModule was added due to https://github.com/google/dagger/issues/783.
 */
@Singleton
@Component(
    modules = [AndroidInjectionModule::class, AndroidSupportInjectionModule::class, AppModule::class,
        MainActivityModule::class, MovieDetailsActivityModule::class, NowPlayingMovieListFragmentModule::class,
        FavoriteMovieListFragmentModule::class, SettingsFragmentModule::class, ViewModelsModule::class,
        ModelsModule::class, LocalRepositoryModule::class, MovieDbServiceModule::class]
)
interface AppComponent : AndroidInjector<MovieBrowserApplication> {

    /**
     * Component builder.
     */
    @Component.Builder
    interface Builder {

        /**
         * Builder for the component.
         * @return the component instance.
         */
        fun build(): AppComponent

        /**
         * Module dependency that need to be created manually and passed in.
         * @param appModule instance of the AppModule.
         * @return the builder instance so that the call can be chained.
         */
        fun appModule(appModule: AppModule): Builder
    }
}
