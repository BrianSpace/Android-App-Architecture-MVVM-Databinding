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

package com.github.brianspace.moviebrowser.repository.local;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;
import com.github.brianspace.moviebrowser.di.qualifiers.ApplicationContext;
import com.github.brianspace.moviebrowser.repository.Constants;
import com.github.brianspace.moviebrowser.repository.IFavoriteStore;
import com.github.brianspace.moviebrowser.repository.data.MovieData;
import com.github.brianspace.moviebrowser.repository.local.database.Favorite;
import com.github.brianspace.moviebrowser.repository.local.database.FavoriteDao;
import com.github.brianspace.moviebrowser.repository.local.database.FavoriteDatabase;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * SQLite FavoriteStore as the favorite store.
 */
@Singleton
class FavoriteStore implements IFavoriteStore {

    // region Private Constants

    /**
     * Suffix of database journal file.
     */
    private static final String DATABASE_JOURNAL_SUFFIX = "-journal";

    // endregion

    // region Private Fields

    /**
     * Application context.
     */
    private final Context appContext;

    /**
     * The ROOM database for favorite movies.
     */
    private FavoriteDatabase favoriteDatabase;

    // endregion

    // region Constructors

    /**
     * Constructor.
     *
     * @param context Context
     */
    @Inject
    /* default */ FavoriteStore(@ApplicationContext final Context context) {
        this.appContext = context;
        buildFavoriteDatabase();
    }

    // endregion

    // region Public Overrides

    @Override
    public boolean clearData() {
        boolean result = true;

        favoriteDatabase.close();

        final File databaseFile = appContext.getDatabasePath(Constants.DATABASE_NAME);
        if (databaseFile.exists()) {
            result = databaseFile.delete();
        }

        final File databaseJournalFile = appContext.getDatabasePath(Constants.DATABASE_NAME + DATABASE_JOURNAL_SUFFIX);
        if (databaseJournalFile.exists()) {
            result = result && databaseJournalFile.delete();
        }

        buildFavoriteDatabase();

        return result;
    }

    @Override
    public Single<Boolean> addFavoriteMovie(@NonNull final MovieData movie) {
        return Single.fromCallable(() -> {
            final FavoriteDao dao = favoriteDatabase.getDao();
            return dao.insertFavorite(new Favorite(movie)) > 0;
        });
    }

    @Override
    public Maybe<MovieData> getFavoriteMovie(final long favoriteId) {
        final FavoriteDao dao = favoriteDatabase.getDao();
        return dao.findFavoriteById(favoriteId).map(Favorite::toMovie);
    }

    @Override
    public Single<List<MovieData>> getAllFavoriteMovies() {
        final FavoriteDao dao = favoriteDatabase.getDao();
        return Single.fromCallable(() -> {
            final List<MovieData> movies = new ArrayList<>();
            for (final Favorite favorite : dao.loadAllFavorites()) {
                movies.add(favorite.toMovie());
            }

            return movies;
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> deleteFavoriteMovie(@NonNull final MovieData movie) {
        return Single.fromCallable(() -> {
            final FavoriteDao dao = favoriteDatabase.getDao();
            return dao.deleteFavorite(movie.getId()) == 1;
        });
    }

    // endregion

    // region Private Methods

    private void buildFavoriteDatabase() {
        favoriteDatabase = Room.databaseBuilder(appContext, FavoriteDatabase.class, Constants.DATABASE_NAME).build();
    }

    // endregion
}
