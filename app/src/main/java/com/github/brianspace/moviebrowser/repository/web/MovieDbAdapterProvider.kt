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

import com.github.brianspace.moviebrowser.BuildConfig
import com.github.brianspace.moviebrowser.repository.Constants
import com.github.brianspace.moviebrowser.repository.util.IDirUtil
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Provider to create [IMovieDbApi] adapter.
 */
internal object MovieDbAdapterProvider {

    // region Private Constants

    /**
     * The endpoint for TMDb web API.
     */
    private const val API_ENDPOINT = "https://api.themoviedb.org/3/"

    /**
     * The query key for the API key parameter to call the TMDb web API.
     */
    private const val QUERY_API_KEY = "api_key"

    /**
     * The API key to call the TMDb web API.
     * @see [Authentication](https://developers.themoviedb.org/3/getting-started/authentication)
     */
    private const val API_KEY = BuildConfig.API_KEY

    // endregion

    // region Private Inner Types

    /**
     * Interceptor to add API key in the request.
     */
    private class ApiKeyInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var original = chain.request()
            val url = original.url().newBuilder().addQueryParameter(QUERY_API_KEY, API_KEY).build()
            original = original.newBuilder().url(url).build()
            return chain.proceed(original)
        }
    }

    // endregion

    // region Package Private Methods

    /**
     * Create a new instance of [okhttp3.OkHttpClient].
     */
    fun createOkHttpClient(dirUtil: IDirUtil): OkHttpClient {
        // Configure HTTP cache
        val httpCacheDirectory = dirUtil.httpCacheDir
        val cache = Cache(httpCacheDirectory, Constants.HTTP_CACHE_SIZE.toLong())

        val builder = OkHttpClient().newBuilder()
            .addInterceptor(ApiKeyInterceptor())
            .connectTimeout(Constants.TMDB_API_TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(Constants.TMDB_API_TIMEOUT_READ, TimeUnit.SECONDS)
            .cache(cache)

        // Uncomment to use Stetho network debugging
        // if (BuildConfig.DEBUG) {
        //     builder.addNetworkInterceptor(com.facebook.stetho.okhttp3.StethoInterceptor()).build();
        // }

        return builder.build()
    }

    /**
     * Create a new instance for [IMovieDbApi] interface.
     * The requests will run in OkHttp's internal thread pool.
     */
    fun create(httpClient: OkHttpClient): IMovieDbApi {
        // Set GSON naming policy
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(API_ENDPOINT)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync()) // Use OkHttp's internal thread pool.
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build()

        return retrofit.create(IMovieDbApi::class.java)
    }

    // endregion
}
