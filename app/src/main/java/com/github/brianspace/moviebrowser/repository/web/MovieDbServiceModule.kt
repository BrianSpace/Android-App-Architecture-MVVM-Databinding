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

package com.github.brianspace.moviebrowser.repository.web

import com.github.brianspace.moviebrowser.repository.IMovieDbService
import com.github.brianspace.moviebrowser.repository.util.IDirUtil
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import okhttp3.OkHttpClient

/**
 * Dependency Injection Module (based on Dagger-2).
 */
@Module
class MovieDbServiceModule {
    @Provides
    @Singleton
    internal fun provideOkHttpClient(dirUtil: IDirUtil): OkHttpClient {
        return MovieDbAdapterProvider.createOkHttpClient(dirUtil)
    }

    @Provides
    @Singleton
    internal fun provideMovieDbApi(okHttpClient: OkHttpClient): IMovieDbApi {
        return MovieDbAdapterProvider.create(okHttpClient)
    }

    @Provides
    @Singleton
    internal fun provideMovieDbService(
        okHttpClient: OkHttpClient,
        movieDbApi: IMovieDbApi
    ): IMovieDbService {
        return MovieDbService(okHttpClient, movieDbApi)
    }
}
