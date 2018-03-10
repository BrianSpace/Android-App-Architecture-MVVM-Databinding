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
import android.support.annotation.Nullable;
import android.util.Log;
import com.github.brianspace.moviebrowser.repository.IMovieDbService;
import com.github.brianspace.moviebrowser.repository.data.Configuration;
import com.github.brianspace.moviebrowser.repository.data.MovieData;
import com.github.brianspace.moviebrowser.repository.data.MovieDetailsData;
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;

/**
 * Accessing TMDb Web API with {@link IMovieDbApi IMovieDbApi}.
 */
@Singleton
class MovieDbService implements IMovieDbService {

    // region Private Constants

    /**
     * Tag for logcat.
     */
    private static final String TAG = MovieDbService.class.getSimpleName();

    /**
     * OkHttpClient for requesting TMDb Web API. Used to clear cache.
     */
    private final OkHttpClient okHttpClient;

    /**
     * IMovieDbApi instance.
     */
    private final IMovieDbApi movieDbApi;

    /**
     * Default empty result for paged movie list.
     */
    private final PagingEnvelope<MovieData> emptyResult = new PagingEnvelope<>(0, 0, 0, null);

    // endregion

    // region Constructors

    /**
     * Constructor.
     */
    @Inject
    /* default */ MovieDbService(@NonNull final OkHttpClient okHttpClient, @NonNull final IMovieDbApi movieDbApi) {
        this.okHttpClient = okHttpClient;
        this.movieDbApi = movieDbApi;
    }

    // endregion

    // region Public Overrides

    @Override
    public boolean clearCache() {
        try {
            okHttpClient.cache().evictAll();
            return true;
        } catch (final IOException e) {
            Log.e(TAG, "clearCache failed: " + e.toString());
        }

        return false;
    }

    @Override
    @NonNull
    public Single<Configuration> getConfiguration() {
        return movieDbApi.getConfiguration();
    }

    @Override
    @NonNull
    public Single<PagingEnvelope<MovieData>> getMovieNowPlaying(
            @Nullable final PagingEnvelope<MovieData> previous) {
        if (previous == null) {
            return movieDbApi.getMovieNowPlaying(1);
        }

        final int prevPage = previous.getPage();
        if (prevPage < previous.getTotalPages()) {
            return movieDbApi.getMovieNowPlaying(prevPage + 1).subscribeOn(Schedulers.io());
        }

        return Single.just(emptyResult);
    }

    @Override
    @NonNull
    public Single<MovieDetailsData> getMovieDetails(final int id) {
        return movieDbApi.getMovieDetails(id);
    }

    @Override
    @NonNull
    public Single<PagingEnvelope<MovieData>> getSimilarMovies(final int id,
            @Nullable final PagingEnvelope<MovieData> previous) {
        if (previous == null) {
            return movieDbApi.getSimilarMovies(id, 1);
        }

        final int prevPage = previous.getPage();
        if (prevPage < previous.getTotalPages()) {
            return movieDbApi.getSimilarMovies(id, prevPage + 1);
        }

        return Single.just(emptyResult);
    }

    // endregion
}
