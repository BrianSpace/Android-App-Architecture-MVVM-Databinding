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

import android.arch.persistence.room.Room
import android.content.Context
import com.github.brianspace.moviebrowser.di.qualifiers.ApplicationContext
import com.github.brianspace.moviebrowser.repository.Constants
import com.github.brianspace.moviebrowser.repository.IFavoriteStore
import com.github.brianspace.moviebrowser.repository.data.MovieData
import com.github.brianspace.moviebrowser.repository.local.database.Favorite
import com.github.brianspace.moviebrowser.repository.local.database.FavoriteDatabase
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


// region Private Constants

/**
 * Suffix of database journal file.
 */
private const val DATABASE_JOURNAL_SUFFIX = "-journal"

// endregion


/**
 * SQLite FavoriteStore as the favorite store.
 */
@Singleton
internal class FavoriteStore
@Inject
constructor(@param:ApplicationContext private val appContext: Context) : IFavoriteStore {

    // region Private Constants

    /**
     * The ROOM database for favorite movies.
     */
    private var favoriteDatabase: FavoriteDatabase = buildDatabase()

    // endregion

    // region Public Overrides

    override val allFavoriteMovies: Single<List<MovieData>>
        get() {
            return Single.fromCallable {
                favoriteDatabase.dao.loadAllFavorites().map { it.toMovie() }
            }.subscribeOn(Schedulers.io())
        }

    override fun clearData(): Boolean {
        var result = true

        favoriteDatabase.close()

        val databaseFile = appContext.getDatabasePath(Constants.DATABASE_NAME)
        if (databaseFile.exists()) {
            result = databaseFile.delete()
        }

        val databaseJournalFile = appContext.getDatabasePath(Constants.DATABASE_NAME + DATABASE_JOURNAL_SUFFIX)
        if (databaseJournalFile.exists()) {
            result = result && databaseJournalFile.delete()
        }

        favoriteDatabase = buildDatabase()

        return result
    }

    override fun addFavoriteMovie(movie: MovieData): Single<Boolean> {
        return Single.fromCallable {
            favoriteDatabase.dao.insertFavorite(Favorite(movie)) > 0
        }
    }

    override fun getFavoriteMovie(favoriteId: Long): Maybe<MovieData> {
        return favoriteDatabase.dao.findFavoriteById(favoriteId).map { it.toMovie() }
    }

    override fun deleteFavoriteMovie(movie: MovieData): Single<Boolean> {
        return Single.fromCallable {
            favoriteDatabase.dao.deleteFavorite(movie.id.toLong()) == 1
        }
    }

    // endregion

    // region Private Methods

    private fun buildDatabase(): FavoriteDatabase {
        return Room.databaseBuilder(appContext, FavoriteDatabase::class.java, Constants.DATABASE_NAME).build()
    }

    // endregion
}
