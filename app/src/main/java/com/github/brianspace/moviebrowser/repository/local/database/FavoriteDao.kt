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

package com.github.brianspace.moviebrowser.repository.local.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Maybe

/**
 * ROOM DAO interface to access the favorite movies database records.
 */
@Dao
interface FavoriteDao {

    /**
     * Insert a new favorite movie record.
     *
     * @param favorite The favorite entity.
     * @return the new rowId of the inserted item.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favorite: Favorite): Long

    /**
     * Load all favorite records.
     *
     * @return The RxJava `Flowable` of all favorite movies.
     */
    @Query("SELECT * FROM Favorite ORDER BY create_time DESC")
    fun loadAllFavorites(): Array<Favorite>

    /**
     * Find the favorite movie record by ID.
     *
     * @param id the movie ID.
     * @return RxJava `Single` of the movie.
     */
    @Query("SELECT * FROM Favorite WHERE id IS :id")
    fun findFavoriteById(id: Long): Maybe<Favorite>

    /**
     * Delete the favorite movie with specified ID.
     *
     * @param id movie ID.
     * @return number of rows deleted.
     */
    @Query("DELETE FROM Favorite WHERE id IS :id")
    fun deleteFavorite(id: Long): Int
}