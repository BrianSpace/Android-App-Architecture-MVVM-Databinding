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

package com.github.brianspace.moviebrowser.di.modules;

import android.app.Application;
import android.content.Context;
import com.github.brianspace.moviebrowser.di.qualifiers.ApplicationContext;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the application.
 */
@Module
public class AppModule {

    /**
     * Application instance.
     */
    private final Application application;

    /**
     * Constructor for AppModule.
     * @param application the application instance.
     */
    public AppModule(final Application application) {
        this.application = application;
    }

    /**
     * Provide application context.
     */
    @Provides
    @ApplicationContext
    public Context provideAppContext() {
        return application;
    }
}
