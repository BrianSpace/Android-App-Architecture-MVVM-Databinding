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

package com.github.brianspace.moviebrowser.viewmodels

import com.github.brianspace.moviebrowser.models.IEntityStore
import com.github.brianspace.moviebrowser.models.IFavoriteMovieCollection
import com.github.brianspace.moviebrowser.models.IImageConfig
import com.github.brianspace.moviebrowser.models.IMovieCollection
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * Dagger Module for models.
 */
@Module
class ViewModelsModule {
    @Provides
    @Singleton
    internal fun provideViewModelRepository(
        imageConfig: IImageConfig, entityStore: IEntityStore,
        favoriteMovieCollection: IFavoriteMovieCollection
    ): IViewModelFactory {
        return ViewModelFactory(imageConfig, entityStore, favoriteMovieCollection)
    }

    @Provides
    @Singleton
    @Named("NowPlaying")
    internal fun provideNowPlayingMovieList(
        @Named("NowPlaying") nowPlayingMovieCollection: IMovieCollection,
        factory: IViewModelFactory
    ): IMovieList {
        return MoviesViewModel(nowPlayingMovieCollection, factory)
    }

    @Provides
    @Singleton
    @Named("Favorites")
    internal fun provideFavoriteMovieList(
        @Named("Favorites") favoriteMovieCollection: IMovieCollection,
        factory: IViewModelFactory
    ): IMovieList {
        return MoviesViewModel(favoriteMovieCollection, factory)
    }
}
