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

package com.github.brianspace.moviebrowser.ui.nav;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.github.brianspace.moviebrowser.BuildConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper for navigation between pages through URI.
 */
public final class NavigationHelper {
    // region Private Constants

    /**
     * Scheme for navigation URIs.
     */
    private static final String URI_SCHEME = BuildConfig.URI_SCHEME;
    /**
     * Hostname for navigation URIs.
     */
    private static final String URI_HOST = BuildConfig.URI_HOST;

    /**
     * Constants: backslash.
     */
    private static final char BACK_SLASH = '/';

    // endregion

    // region Public Constants

    /**
     * Common Queries.
     */
    public static final String QUERY_ID = "id"; // movie ID

    /**
     * Path for homepage (MainActivity).
     */
    public static final String PATH_HOME = "home";
    /**
     * Path for now playing movies (in MainActivity).
     */
    public static final String PATH_HOME_NOW_PLAYING = PATH_HOME + "/now_playing";
    /**
     * Path for favorite movies (in MainActivity).
     */
    public static final String PATH_HOME_FAVORITES = PATH_HOME + "/favorites";
    /**
     * Path for movie details (in MovieDetailsActivity).
     */
    public static final String PATH_DETAILS = "details";

    // endregion

    // region Public Inner Types

    /**
     * Parsed parts of the navigation URI.
     */
    public static class NavUriParts {

        /**
         * Uri.
         */
        public Uri uri;

        /**
         * Path without leading '/'. If not null then the uri is also not null.
         */
        public String pathNoLeadingSlash;
    }

    // endregion

    // region Constructors

    private NavigationHelper() throws InstantiationException {
        throw new InstantiationException("Utility class NavigationHelper should not be instantiated!");
    }

    // endregion

    // region Public Static Methods

    /**
     * Extract Uri whose scheme is URI_SCHEME and authority is URI_HOST, and path without leading '/'.
     * @param activity      The Activity that needs to extract Uri
     * @return              Extracted Uri and path
     */
    @NonNull
    public static NavUriParts getValidNavUri(@NonNull final Activity activity) {
        final NavUriParts parts = new NavUriParts();

        final Uri uri = getUriFromIntent(activity.getIntent());
        if (uri == null) {
            return parts;
        }

        if (uri.getScheme().equals(URI_SCHEME) && uri.getAuthority().equals(URI_HOST)) {
            parts.uri = uri;
            String uriPath = uri.getPath();
            if (uriPath == null || uriPath.isEmpty()) {
                return parts;
            }

            if (uriPath.charAt(0) == BACK_SLASH) {
                uriPath = uriPath.substring(1); // Remove leading '/'
            }

            parts.pathNoLeadingSlash = uriPath;
        }

        return parts;
    }

    /**
     * Navigate to a URI path, with queries.
     * @param context Context.
     * @param path URI path.
     * @param queries Additional queries.
     */
    public static void navigateToPath(@NonNull final Context context,
                                      @NonNull final String path,
                                      @Nullable final Map<String, String> queries) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, buildUri(path, queries));
        if (path.equals(PATH_HOME_NOW_PLAYING) || path.equals(PATH_HOME_FAVORITES)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }

    /**
     * Navigate to movie details page.
     * @param context Context
     * @param id Movie ID
     */
    public static void navigateToMovieDetails(@NonNull final Context context, final int id) {
        final HashMap<String, String> queries = new HashMap<>();
        queries.put(QUERY_ID, String.valueOf(id));
        navigateToPath(context, PATH_DETAILS, queries);
    }

    // endregion

    // region Private Methods

    @Nullable
    private static Uri getUriFromIntent(@Nullable final Intent intent) {
        if (intent == null) {
            return null;
        }

        final String action = intent.getAction();
        if (action == null || !action.equals(Intent.ACTION_VIEW)) {
            return null;
        }

        return intent.getData();
    }

    private static Uri buildUri(@NonNull final String path, @Nullable final Map<String, String> queries) {
        final Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_SCHEME).authority(URI_HOST).appendPath(path);
        if (queries != null && !queries.isEmpty()) {
            for (final Map.Entry<String, String> entry : queries.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        return builder.build();
    }

    // endregion
}

