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

import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.github.brianspace.moviebrowser.repository.IFavoriteStore;
import com.github.brianspace.moviebrowser.repository.IMovieDbService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Used to clean data (HTTP cache, image cache, favorite database, etc.).
 */
@Singleton
public class DataCleaner {
    // region Private Constants

    /**
     * Tag for logcat.
     */
    private static final String TAG = DataCleaner.class.getSimpleName();

    /**
     * Set a minimum interval between the clearing events so that the messages will not change or disappear too fast.
     */
    private static final int MIN_TASK_DURATION = 800;

    // endregion

    // region Public Inner Types

    /**
     * The stages of the clearing task.
     */
    public enum Stage {
        HTTP_CACHE,
        IMAGE_CACHE,
        FAVORITES,
        COMPLETE
    }

    // endregion

    // region Private Fields

    /**
     * The favorite movie collection.
     */
    private final FavoriteMovieCollection favoriteMovieCollection;

    /**
     * Interface to access the favorite store.
     */
    private final IFavoriteStore favoriteStore;

    /**
     * Interface to request TMDb Web API. Used to clear cache.
     */
    private final IMovieDbService movieDbService;

    /**
     * Instance of Glide, used to clear image cache.
     */
    private final Glide glide;

    /**
     * Mapping function to calculate the delay time so that the message will not disappear too fast.
     */
    private final Function<Timed<Stage>, Timed<Stage>> delayTimeCalculator = item -> {
        Log.d(TAG, "map: " + item.toString());
        final long delayTime = MIN_TASK_DURATION - item.time(TimeUnit.MILLISECONDS);
        final long acturalDelayTime = delayTime > 0 ? delayTime : 0;
        return new Timed<>(item.value(), acturalDelayTime, TimeUnit.MILLISECONDS);
    };

    /**
     * Aggregate the delay time so the events firing can be delayed.
     */
    private final BiFunction<Timed<Stage>, Timed<Stage>, Timed<Stage>> delayAggregation = (prev, current) -> {
        Log.d(TAG, "scan: " + prev.toString() + current.toString());
        return new Timed<>(current.value(), prev.time() + current.time(), current.unit());
    };

    // endregion

    // region Constructors

    /**
     * Create a new instance of DataCleaner.
     *
     * @param favoriteStore     interface to clear the favorite store.
     * @param movieDbService    interface for TMDb Web API, used to clear HTTP cache.
     * @param glide             instance of Glide, used to clear image cache.
     */
    @Inject
    /* default */ DataCleaner(final FavoriteMovieCollection favoriteMovieCollection,
            @NonNull final IFavoriteStore favoriteStore, @NonNull final IMovieDbService movieDbService,
            @NonNull final Glide glide) {
        this.favoriteMovieCollection = favoriteMovieCollection;
        this.favoriteStore = favoriteStore;
        this.movieDbService = movieDbService;
        this.glide = glide;
    }

    // endregion

    // region Public Methods

    /**
     * Clear application data.
     *
     * @param clearFavorites true to clear favorite movies, otherwise or not.
     * @return  RxJava Observable for the timed stages.
     */
    public Observable<Timed<Stage>> clearData(final boolean clearFavorites) {
        final ObservableOnSubscribe<Stage> clearingTasks = (ObservableEmitter<Stage> emitter) -> {
            if (clearFavorites) {
                // Clear favorites
                emitter.onNext(Stage.FAVORITES);
                favoriteMovieCollection.clear();
                favoriteStore.clearData();
            }

            // Clear HTTP cache
            emitter.onNext(Stage.HTTP_CACHE);
            movieDbService.clearCache();

            // Clear image cache
            emitter.onNext(Stage.IMAGE_CACHE);
            glide.clearDiskCache();
            emitter.onNext(Stage.COMPLETE); // Emit an extra event to delay the onComplete event.
            emitter.onComplete();
        };

        return Observable.create(clearingTasks)
                .timeInterval()
                .map(delayTimeCalculator)
                .scan(delayAggregation)
                .delay(item -> Observable.timer(item.time(), TimeUnit.MILLISECONDS))
                .subscribeOn(Schedulers.io());
    }

    // endregion
}