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

package com.github.brianspace.moviebrowser.ui.activity

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v13.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager.OnPageChangeListener
import com.bumptech.glide.Glide
import com.github.brianspace.moviebrowser.R
import com.github.brianspace.moviebrowser.ui.fragment.FavoriteMovieListFragment
import com.github.brianspace.moviebrowser.ui.fragment.NowPlayingMovieListFragment
import com.github.brianspace.moviebrowser.ui.fragment.SettingsFragment
import dagger.android.support.DaggerAppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*

/**
 * The application homepage.
 */
class MainActivity : DaggerAppCompatActivity() {

    // region Package Private Fields

    /**
     * "Now Playing" resource string used for tab title.
     */
    private val tabTitleNowPlaying by lazy {
        getString(R.string.tab_now_playing)
    }

    /**
     * "Favorites" resource string used for tab title.
     */
    private val tabTitleFavorite by lazy {
        getString(R.string.tab_favorite)
    }

    /**
     * "Settings" resource string used for tab title.
     */
    private val tabTitleSettings by lazy {
        getString(R.string.tab_settings)
    }

    // endregion

    // region Private Fields

    /**
     * Listener used to change the selected item in the bottom navigation view when the page is switched.
     */
    private val onPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            // ignore
        }

        override fun onPageSelected(position: Int) {
            val itemId: Int = when (position) {
                0 -> R.id.navigation_home
                1 -> R.id.navigation_favorites
                2 -> R.id.navigation_settings
                else -> return
            }

            bottomNavigationView.selectedItemId = itemId
        }

        override fun onPageScrollStateChanged(state: Int) {
            // ignore
        }
    }

    // endregion

    // region Private Inner Types

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private inner class SectionsPagerAdapter/* default */ internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> NowPlayingMovieListFragment()
                1 -> FavoriteMovieListFragment()
                2 -> SettingsFragment()
                else -> null
            }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> tabTitleNowPlaying
                1 -> tabTitleFavorite
                2 -> tabTitleSettings
                else -> null
            }
        }
    }

    // endregion

    // region Public Overrides

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNavigationView()
    }

    public override fun onStop() {
        // Stop Glide to prevent memory leaks.
        // Use Glide.with(this).onDestroy() in onDestroy will cause exceptions.
        Glide.with(this).pauseRequests()
        super.onStop()
    }

    // endregion

    // region Private Methods

    private fun initNavigationView() {
        val sectionsPagerAdapter = SectionsPagerAdapter(fragmentManager)
        viewPager!!.adapter = sectionsPagerAdapter
        viewPager!!.addOnPageChangeListener(onPageChangeListener)
        bottomNavigationView!!.setOnNavigationItemSelectedListener listener@ { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    viewPager!!.currentItem = 0
                    return@listener true
                }
                R.id.navigation_favorites -> {
                    viewPager!!.currentItem = 1
                    return@listener true
                }
                R.id.navigation_settings -> {
                    viewPager!!.currentItem = 2
                    return@listener true
                }
                else -> return@listener false
            }
        }
    }

    // endregion
}
