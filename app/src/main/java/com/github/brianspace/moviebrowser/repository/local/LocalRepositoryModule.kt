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

package com.github.brianspace.moviebrowser.repository.local

import android.content.Context
import com.bumptech.glide.Glide
import com.github.brianspace.moviebrowser.di.qualifiers.ApplicationContext
import com.github.brianspace.moviebrowser.repository.IConfigStore
import com.github.brianspace.moviebrowser.repository.IFavoriteStore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger module for local repository (database and shared preferences).
 */
@Module
class LocalRepositoryModule {
    @Provides
    @Singleton
    internal fun provideFavoriteStore(@ApplicationContext context: Context): IFavoriteStore {
        return FavoriteStore(context)
    }

    @Provides
    @Singleton
    internal fun provideConfigStore(@ApplicationContext context: Context): IConfigStore {
        return ConfigStore(context)
    }

    @Provides
    @Singleton
    internal fun provideGlide(@ApplicationContext context: Context): Glide {
        return Glide.get(context)
    }
}
