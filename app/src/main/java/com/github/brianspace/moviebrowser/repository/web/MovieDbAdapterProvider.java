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

package com.github.brianspace.moviebrowser.repository.web;

import android.support.annotation.NonNull;
import com.github.brianspace.moviebrowser.BuildConfig;
import com.github.brianspace.moviebrowser.repository.Constants;
import com.github.brianspace.moviebrowser.repository.util.FileUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provider to create {@link IMovieDbApi IMovieDbApi} adapter.
 */
final class MovieDbAdapterProvider {

    // region Private Constants

    /**
     * The endpoint for TMDb web API.
     */
    private static final String API_ENDPOINT = "https://api.themoviedb.org/3/";

    /**
     * The query key for the API key parameter to call the TMDb web API.
     */
    private static final String QUERY_API_KEY = "api_key";

    /**
     * The API key to call the TMDb web API.
     * @see <a href="https://developers.themoviedb.org/3/getting-started/authentication">Authentication</a>
     */
    private static final String API_KEY = BuildConfig.API_KEY;

    // endregion

    // region Private Inner Types

    /**
     * Interceptor to add API key in the request.
     */
    private static class ApiKeyInterceptor implements Interceptor {
        @Override
        public Response intercept(final Chain chain) throws IOException {
            Request original = chain.request();
            final HttpUrl url = original.url().newBuilder().addQueryParameter(QUERY_API_KEY, API_KEY).build();
            original = original.newBuilder().url(url).build();
            return chain.proceed(original);
        }
    }

    // endregion

    // region Constructors

    private MovieDbAdapterProvider() throws InstantiationException {
        throw new InstantiationException("Utility class MovieDbAdapterProvider should not be instantiated!");
    }

    // endregion

    // region Package Private Methods

    /**
     * Create a new instance of {@link okhttp3.OkHttpClient}.
     */
    @NonNull
    /* default */ static OkHttpClient createOkHttpClient() {
        // Configure HTTP cache
        final File httpCacheDirectory = FileUtil.getHttpCacheDir();
        final Cache cache = new Cache(httpCacheDirectory, Constants.HTTP_CACHE_SIZE);

        final OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .addInterceptor(new ApiKeyInterceptor())
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .cache(cache);

        // Uncomment to use Stetho network debugging
        // if (BuildConfig.DEBUG) {
        //     builder.addNetworkInterceptor(
        //            new com.facebook.stetho.okhttp3.StethoInterceptor()).build();
        // }

        return builder.build();
    }

    /**
     * Create a new instance for {@link IMovieDbApi IMovieDbApi} interface.
     * The requests will run in OkHttp's internal thread pool.
     */
    @NonNull
    /* default */ static IMovieDbApi create(@NonNull final OkHttpClient httpClient) {
        // Set GSON naming policy
        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync()) // Use OkHttp's internal thread pool.
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();

        return retrofit.create(IMovieDbApi.class);
    }

    // endregion
}
