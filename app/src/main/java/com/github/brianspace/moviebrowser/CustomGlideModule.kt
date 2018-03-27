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

package com.github.brianspace.moviebrowser

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

/**
 * Connect timeout (in seconds) for Glide.
 */
private const val CONNECT_TIMEOUT = 15L

/**
 * Read timeout (in seconds) for Glide.
 */
private const val READ_TIMEOUT = 30L

/**
 * Glide Configurations.
 */
@GlideModule
class CustomGlideModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false // Disable manifest parsing.
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(ExternalPreferredCacheDiskCacheFactory(context))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val builder = OkHttpClient().newBuilder()
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

        // Uncomment to use Stetho network debugging
        // if (BuildConfig.DEBUG) {
        //     builder.addNetworkInterceptor(com.facebook.stetho.okhttp3.StethoInterceptor()).build()
        // }

        val client = builder.build()
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))
    }
}

