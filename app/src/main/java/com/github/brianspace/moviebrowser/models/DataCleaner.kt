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

package com.github.brianspace.moviebrowser.models

import android.util.Log
import com.bumptech.glide.Glide
import com.github.brianspace.moviebrowser.repository.IFavoriteStore
import com.github.brianspace.moviebrowser.repository.IMovieDbService
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Timed
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


// region Private Constants

/**
 * Tag for logcat.
 */
private val TAG = DataCleaner::class.java.simpleName

/**
 * Set a minimum interval between the clearing events so that the messages will not change or disappear too fast.
 */
private const val MIN_TASK_DURATION = 800

// endregion

/**
 * Used to clean data (HTTP cache, image cache, favorite database, etc.).
 */
@Singleton
class DataCleaner
/**
 * Create a new instance of DataCleaner.
 *
 * @param favoriteStore     interface to clear the favorite store.
 * @param movieDbService    interface for TMDb Web API, used to clear HTTP cache.
 * @param glide             instance of Glide, used to clear image cache.
 */
@Inject
internal constructor(
    /**
     * The favorite movie collection.
     */
    private val favoriteMovieCollection: FavoriteMovieCollection,
    /**
     * Interface to access the favorite store.
     */
    private val favoriteStore: IFavoriteStore,
    /**
     * Interface to request TMDb Web API. Used to clear cache.
     */
    private val movieDbService: IMovieDbService,
    /**
     * Instance of Glide, used to clear image cache.
     */
    private val glide: Glide
) {

    // region Private Properties

    /**
     * Mapping function to calculate the delay time so that the message will not disappear too fast.
     */
    private val delayTimeCalculator = { item: Timed<Stage> ->
        Log.d(TAG, "map: " + item.toString())
        val delayTime = MIN_TASK_DURATION - item.time(TimeUnit.MILLISECONDS)
        val acturalDelayTime = if (delayTime > 0) delayTime else 0
        Timed(item.value(), acturalDelayTime, TimeUnit.MILLISECONDS)
    }

    /**
     * Aggregate the delay time so the events firing can be delayed.
     */
    private val delayAggregation = { prev: Timed<Stage>, current: Timed<Stage> ->
        Log.d(TAG, "scan: " + prev.toString() + current.toString())
        Timed(current.value(), prev.time() + current.time(), current.unit())
    }

    // endregion

    // region Public Inner Types

    /**
     * The stages of the clearing task.
     */
    enum class Stage {
        HTTP_CACHE,
        IMAGE_CACHE,
        FAVORITES,
        COMPLETE
    }

    // endregion

    // region Public Methods

    /**
     * Clear application data.
     *
     * @param clearFavorites true to clear favorite movies, otherwise or not.
     * @return  RxJava Observable for the timed stages.
     */
    fun clearData(clearFavorites: Boolean): Observable<Timed<Stage>> {
        val clearingTasks = { emitter: ObservableEmitter<Stage> ->
            if (clearFavorites) {
                // Clear favorites
                emitter.onNext(Stage.FAVORITES)
                favoriteMovieCollection.clear()
                favoriteStore.clearData()
            }

            // Clear HTTP cache
            emitter.onNext(Stage.HTTP_CACHE)
            movieDbService.clearCache()

            // Clear image cache
            emitter.onNext(Stage.IMAGE_CACHE)
            glide.clearDiskCache()
            emitter.onNext(Stage.COMPLETE) // Emit an extra event to delay the onComplete event.
            emitter.onComplete()
        }

        return Observable.create(clearingTasks)
            .timeInterval()
            .map(delayTimeCalculator)
            .scan(delayAggregation)
            .delay { item -> Observable.timer(item.time(), TimeUnit.MILLISECONDS) }
            .subscribeOn(Schedulers.io())
    }

    // endregion
}