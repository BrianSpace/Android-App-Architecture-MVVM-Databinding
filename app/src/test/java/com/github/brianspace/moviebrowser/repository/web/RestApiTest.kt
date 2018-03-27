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

package com.github.brianspace.moviebrowser.repository.web

import android.content.Context
import com.github.brianspace.moviebrowser.repository.IMovieDbService
import com.github.brianspace.moviebrowser.repository.util.DirUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.File

/**
 * Test with real TMDb API.
 */
@Ignore
class RestApiTest {

    private val service: IMovieDbService

    init {
        val mockContext = mock(Context::class.java)
        val mockAppContext = mock(Context::class.java)
        `when`(mockContext.applicationContext).thenReturn(mockAppContext)
        `when`(mockAppContext.externalCacheDir).thenReturn(File("./cache/"))

        val serviceModule = MovieDbServiceModule()
        val dirUtil = DirUtil(mockContext)
        val okHttpClient = serviceModule.provideOkHttpClient(dirUtil)
        val api = serviceModule.provideMovieDbApi(okHttpClient)
        service = serviceModule.provideMovieDbService(okHttpClient, api)
    }

    @Test
    @Throws(Exception::class)
    fun testConfiguration() {
        val testSubscriber = service.configuration.test()
        testSubscriber.await()
        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        val result = testSubscriber.values()
        assertNotNull("Configuration result is null.", result)
        val config = result[0]
        assertNotNull("Configuration data is null.", config)
        assertNotNull("Configuration for images is null.", config.imageConfig)
        val imageBaseUrl = config.imageConfig?.baseUrl
        assertNotNull("Configuration for image base URL is null.", imageBaseUrl)
    }

    @Test
    @Throws(Exception::class)
    fun testNowPlaying() {
        val testSubscriber = service.getMovieNowPlaying(null).test()
        testSubscriber.await()
        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        val result = testSubscriber.values()
        assertNotNull("Now playing result is null.", result)
        val movies = result[0]
        assertNotNull("Now Playing movie data is null.", movies)
        assertEquals("Page of the Now Playing movie is null.", movies.page.toLong(), 1)
        assertNotNull("Now playing movie's results is null.", movies.results)
    }

    @Test
    @Throws(Exception::class)
    fun testMovieDetails() {
        val id = 209112
        val testSubscriber = service.getMovieDetails(id).test()
        testSubscriber.await()
        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        val result = testSubscriber.values()
        assertNotNull("Movie details result is null.", result)
        val movieDetails = result[0]
        assertNotNull("Movie details data is null.", movieDetails)
        assertEquals("Movie details' ID does not match.", movieDetails.id.toLong(), id.toLong())
        assertNotNull("Movie details' title does not match.", movieDetails.title)
    }

    @Test
    @Throws(Exception::class)
    fun testSimilarMovies() {
        val id = 209112
        val testSubscriber = service.getSimilarMovies(id, null).test()
        testSubscriber.await()
        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        val result = testSubscriber.values()
        assertNotNull("Similar movies result is null.", result)
        val movies = result[0]
        assertNotNull("Similar movies data is null.", movies)
        assertEquals("Similar movies page is null.", movies.page.toLong(), 1)
        assertNotNull("Similar movies' results is null.", movies.results)
    }
}
