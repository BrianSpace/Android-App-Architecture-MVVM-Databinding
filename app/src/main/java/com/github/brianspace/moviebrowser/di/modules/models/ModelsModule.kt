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

package com.github.brianspace.moviebrowser.di.modules.models

import com.github.brianspace.moviebrowser.models.EntityStore
import com.github.brianspace.moviebrowser.models.FavoriteMovieCollection
import com.github.brianspace.moviebrowser.models.IEntityStore
import com.github.brianspace.moviebrowser.models.IFavoriteMovieCollection
import com.github.brianspace.moviebrowser.models.IImageConfig
import com.github.brianspace.moviebrowser.models.IMovieCollection
import com.github.brianspace.moviebrowser.models.NowPlayingMovieCollection
import com.github.brianspace.moviebrowser.models.TmdbConfig
import com.github.brianspace.moviebrowser.repository.IConfigStore
import com.github.brianspace.moviebrowser.repository.IFavoriteStore
import com.github.brianspace.moviebrowser.repository.IMovieDbService
import dagger.Module
import dagger.Provides
import java.lang.ref.SoftReference
import javax.inject.Named
import javax.inject.Singleton

/**
 * Dagger module for model layer.
 */
@Module
class ModelsModule {

    /**
     * Save a single instance for the TmdbConfig.
     * It may implement more interfaces in the future.
     */
    private var tmdbConfigWeakReference: SoftReference<TmdbConfig>? = null

    @Provides
    @Singleton
    internal fun provideImageConfig(
        movieDbService: IMovieDbService,
        configStore: IConfigStore
    ): IImageConfig {
        tmdbConfigWeakReference?.get()?.apply { return this }
        return TmdbConfig(movieDbService, configStore).apply {
            tmdbConfigWeakReference = SoftReference(this)
            init()
        }
    }

    @Provides
    @Singleton
    internal fun provideModelRepository(service: IMovieDbService): IEntityStore {
        return EntityStore(service)
    }

    @Provides
    @Singleton
    internal fun provideFavoriteMovies(
        favoriteStore: IFavoriteStore,
        entityStore: IEntityStore
    ): FavoriteMovieCollection {
        return FavoriteMovieCollection(favoriteStore, entityStore)
    }

    @Provides
    @Singleton
    internal fun provideFavoriteMovieCollection(
        favoriteMovieCollection: FavoriteMovieCollection
    ): IFavoriteMovieCollection {
        return favoriteMovieCollection
    }

    @Provides
    @Singleton
    @Named("NowPlaying")
    internal fun provideNowPlayingMovieList(
        service: IMovieDbService,
        entityStore: IEntityStore
    ): IMovieCollection {
        return NowPlayingMovieCollection(service, entityStore)
    }

    @Provides
    @Singleton
    @Named("Favorites")
    internal fun provideFavoriteMovieList(favorites: IFavoriteMovieCollection): IMovieCollection {
        return favorites
    }
}
