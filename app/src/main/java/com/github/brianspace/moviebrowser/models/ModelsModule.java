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

package com.github.brianspace.moviebrowser.models;

import com.github.brianspace.moviebrowser.repository.IConfigStore;
import com.github.brianspace.moviebrowser.repository.IFavoriteStore;
import com.github.brianspace.moviebrowser.repository.IMovieDbService;
import dagger.Module;
import dagger.Provides;
import java.lang.ref.SoftReference;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Dagger module for model layer.
 */
@SuppressWarnings("PMD.UseUtilityClass")
@Module
public class ModelsModule {

    /**
     * Save a single instance for the TmdbConfig.
     * It may implement more interfaces in the future.
     */
    private static SoftReference<TmdbConfig> tmdbConfigWeakReference;

    @Provides
    @Singleton
    /* default */ static IImageConfig provideImageConfig(final IMovieDbService movieDbService,
            final IConfigStore configStore) {
        if (tmdbConfigWeakReference != null && tmdbConfigWeakReference.get() != null) {
            return tmdbConfigWeakReference.get();
        }

        final TmdbConfig config = new TmdbConfig(movieDbService, configStore);
        tmdbConfigWeakReference = new SoftReference<>(config);
        config.init();
        return config;
    }

    @Provides
    @Singleton
    /* default */ static IEntityStore provideModelRepository(final IMovieDbService service) {
        return new EntityStore(service);
    }

    @Provides
    @Singleton
    /* default */ static FavoriteMovieCollection provideFavoriteMovies(final IFavoriteStore favoriteStore,
            final IEntityStore entityStore) {
        return new FavoriteMovieCollection(favoriteStore, entityStore);
    }

    @Provides
    @Singleton
    /* default */ static IFavoriteMovieCollection provideFavoriteMovieCollection(
            final FavoriteMovieCollection favoriteMovieCollection) {
        return favoriteMovieCollection;
    }

    @Provides
    @Singleton
    @Named("NowPlaying")
    /* default */ static IMovieCollection provideNowPlayingMovieList(final IMovieDbService service,
            final IEntityStore entityStore) {
        return new NowPlayingMovieCollection(service, entityStore);
    }

    @Provides
    @Singleton
    @Named("Favorites")
    /* default */ static IMovieCollection provideFavoriteMovieList(final IFavoriteMovieCollection favorites) {
        return favorites;
    }
}