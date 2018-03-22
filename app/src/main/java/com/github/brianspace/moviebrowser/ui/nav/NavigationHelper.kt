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

@file:JvmName("NavigationHelper")
package com.github.brianspace.moviebrowser.ui.nav

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.github.brianspace.moviebrowser.BuildConfig

/**
 * Helper for navigation between pages through URI.
 */

// region Private Constants

/**
 * Scheme for navigation URIs.
 */
private const val URI_SCHEME = BuildConfig.URI_SCHEME
/**
 * Hostname for navigation URIs.
 */
private const val URI_HOST = BuildConfig.URI_HOST

/**
 * Constants: backslash.
 */
private const val BACK_SLASH = '/'

// endregion

// region Public Types

/**
 * Parsed parts of the navigation URI.
 */
class NavUriParts {

    /**
     * Uri.
     */
    @JvmField
    var uri: Uri? = null

    /**
     * Path without leading '/'. If not null then the uri is also not null.
     */
    var pathNoLeadingSlash: String? = null
}

// endregion

// region Public Constants

/**
 * Common Queries.
 */
const val QUERY_ID = "id" // movie ID

/**
 * Path for homepage (MainActivity).
 */
const val PATH_HOME = "home"

/**
 * Path for now playing movies (in MainActivity).
 */
const val PATH_HOME_NOW_PLAYING = "$PATH_HOME/now_playing"

/**
 * Path for favorite movies (in MainActivity).
 */
const val PATH_HOME_FAVORITES = "$PATH_HOME/favorites"

/**
 * Path for movie details (in MovieDetailsActivity).
 */
const val PATH_DETAILS = "details"

// endregion

// region Public Methods

/**
 * Extract Uri whose scheme is URI_SCHEME and authority is URI_HOST, and path without leading '/'.
 * @receiver The Activity that needs to extract Uri
 * @return Extracted Uri and path
 */
fun Activity.getValidNavUri(): NavUriParts {
    val parts = NavUriParts()

    val uri = getUriFromIntent(this.intent) ?: return parts

    if (uri.scheme == URI_SCHEME && uri.authority == URI_HOST) {
        parts.uri = uri
        var uriPath = uri.path
        if (uriPath == null || uriPath.isEmpty()) {
            return parts
        }

        if (uriPath[0] == BACK_SLASH) {
            uriPath = uriPath.substring(1) // Remove leading '/'
        }

        parts.pathNoLeadingSlash = uriPath
    }

    return parts
}

/**
 * Navigate to a URI path, with queries.
 * @receiver Context.
 * @param path URI path.
 * @param queries Additional queries.
 */
fun Context.navigateToPath(path: String, queries: Map<String, String>?) {
    val intent = Intent(Intent.ACTION_VIEW, buildUri(path, queries))
    if (path == PATH_HOME_NOW_PLAYING || path == PATH_HOME_FAVORITES) {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    } else {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    this.startActivity(intent)
}

/**
 * Navigate to movie details page.
 * @receiver Context
 * @param id Movie ID
 */
fun Context.navigateToMovieDetails(id: Int) {
    navigateToPath(PATH_DETAILS, mapOf(QUERY_ID to id.toString()))
}

// endregion

// region Private Methods

private fun getUriFromIntent(intent: Intent?): Uri? {
    if (intent == null) {
        return null
    }

    val action = intent.action
    return if (action == null || action != Intent.ACTION_VIEW) {
        null
    } else intent.data

}

private fun buildUri(path: String, queries: Map<String, String>?): Uri {
    val builder = Uri.Builder()
    builder.scheme(URI_SCHEME).authority(URI_HOST).appendPath(path)
    if (queries != null && !queries.isEmpty()) {
        for ((key, value) in queries) {
            builder.appendQueryParameter(key, value)
        }
    }

    return builder.build()
}

// endregion

