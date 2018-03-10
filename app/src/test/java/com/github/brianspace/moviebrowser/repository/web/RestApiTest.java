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

package com.github.brianspace.moviebrowser.repository.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import com.github.brianspace.moviebrowser.repository.IMovieDbService;
import com.github.brianspace.moviebrowser.repository.data.Configuration;
import com.github.brianspace.moviebrowser.repository.data.MovieData;
import com.github.brianspace.moviebrowser.repository.data.MovieDetailsData;
import com.github.brianspace.moviebrowser.repository.data.PagingEnvelope;
import com.github.brianspace.moviebrowser.repository.util.FileUtil;
import io.reactivex.observers.TestObserver;
import java.io.File;
import java.util.List;
import okhttp3.OkHttpClient;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 * WARNING: Not Unit test. Test with real TMDb API.
 */
@Ignore
@SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts", "PMD.CommentRequired"})
public class RestApiTest {

    private static IMovieDbService service;

    /**
     * Code to run after the test class is created.
     */
    @BeforeClass
    public static void setUpClass() {
        final Context mockContext = mock(Context.class);
        final Context mockAppContext = mock(Context.class);
        when(mockContext.getApplicationContext()).thenReturn(mockAppContext);
        when(mockAppContext.getExternalCacheDir()).thenReturn(new File("./cache/"));

        FileUtil.init(mockContext);
        final OkHttpClient okHttpClient = MovieDbServiceModule.provideOkHttpClient();
        final IMovieDbApi api = MovieDbServiceModule.provideMovieDbApi(okHttpClient);
        service = MovieDbServiceModule.provideMovieDbService(okHttpClient, api);
    }

    @Test
    public void testConfiguration() throws Exception {
        final TestObserver<Configuration> testSubscriber = service.getConfiguration().test();
        testSubscriber.await();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        final List<Configuration> result = testSubscriber.values();
        assertNotNull("Configuration result is null.", result);
        final Configuration config = result.get(0);
        assertNotNull("Configuration data is null.", config);
        assertNotNull("Configuration for images is null.", config.getImageConfig());
        final String imageBaseUrl = config.getImageConfig().getBaseUrl();
        assertNotNull("Configuration for image base URL is null.", imageBaseUrl);
    }

    @Test
    public void testNowPlaying() throws Exception {
        final TestObserver<PagingEnvelope<MovieData>> testSubscriber = service.getMovieNowPlaying(null).test();
        testSubscriber.await();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        final List<PagingEnvelope<MovieData>> result = testSubscriber.values();
        assertNotNull("Now playing result is null.", result);
        final PagingEnvelope<MovieData> movies = result.get(0);
        assertNotNull("Now Playing movie data is null.", movies);
        assertEquals("Page of the Now Playing movie is null.", movies.getPage(), 1);
        assertNotNull("Now playing movie's results is null.", movies.getResults());
    }

    @Test
    public void testMovieDetails() throws Exception {
        final int id = 209112;
        final TestObserver<MovieDetailsData> testSubscriber = service.getMovieDetails(id).test();
        testSubscriber.await();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        final List<MovieDetailsData> result = testSubscriber.values();
        assertNotNull("Movie details result is null.", result);
        final MovieDetailsData movieDetails = result.get(0);
        assertNotNull("Movie details data is null.", movieDetails);
        assertEquals("Movie details' ID does not match.", movieDetails.getId(), id);
        assertNotNull("Movie details' title does not match.", movieDetails.getTitle());
    }

    @Test
    public void testSimilarMovies() throws Exception {
        final int id = 209112;
        final TestObserver<PagingEnvelope<MovieData>> testSubscriber = service.getSimilarMovies(id, null).test();
        testSubscriber.await();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        final List<PagingEnvelope<MovieData>> result = testSubscriber.values();
        assertNotNull("Similar movies result is null.", result);
        final PagingEnvelope<MovieData> movies = result.get(0);
        assertNotNull("Similar movies data is null.", movies);
        assertEquals("Similar movies page is null.", movies.getPage(), 1);
        assertNotNull("Similar movies' results is null.", movies.getResults());
    }
}
