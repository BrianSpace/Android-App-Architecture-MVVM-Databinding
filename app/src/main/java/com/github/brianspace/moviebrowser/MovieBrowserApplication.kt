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

import android.app.Activity
import android.app.Application
import android.os.StrictMode

import com.github.brianspace.moviebrowser.di.components.AppComponent
import com.github.brianspace.moviebrowser.di.components.DaggerAppComponent
import com.github.brianspace.moviebrowser.di.modules.AppModule
import com.github.brianspace.moviebrowser.repository.util.FileUtil
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Application class for global initialization.
 */
class MovieBrowserApplication : Application(), HasActivityInjector {

    // region Protected/Private Fields

    /**
     * Dagger component for application.
     */
    private lateinit var appComponent: AppComponent

    /**
     * Dagger injector for activities.
     */
    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    // endregion

    // region Public Overrides

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )

            LeakCanary.install(this)
            // Uncomment to debug using Stetho
            // com.facebook.stetho.Stetho.initializeWithDefaults(this)
        }

        init()
    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return dispatchingActivityInjector
    }

    // endregion

    // region Private Methods

    private fun init() {
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        appComponent.inject(this)
        FileUtil.init(this)
    }

    // endregion
}