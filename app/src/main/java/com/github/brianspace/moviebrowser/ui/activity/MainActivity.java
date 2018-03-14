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

package com.github.brianspace.moviebrowser.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.github.brianspace.moviebrowser.R;
import com.github.brianspace.moviebrowser.ui.fragment.FavoriteMovieListFragment;
import com.github.brianspace.moviebrowser.ui.fragment.NowPlayingMovieListFragment;
import com.github.brianspace.moviebrowser.ui.fragment.SettingsFragment;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * The application homepage.
 */
public class MainActivity extends DaggerAppCompatActivity {

    // region Package Private Fields

    /**
     * View pager.
     */
    @BindView(R.id.container)
    /* default */ ViewPager viewPager;

    /**
     * The bottom navigation view.
     */
    @BindView(R.id.navigation_view)
    /* default */ BottomNavigationView bottomNavigationView;

    /**
     * "Now Playing" resource string used for tab title.
     */
    @BindString(R.string.tab_now_playing)
    /* default */ String tabTitleNowPlaying;

    /**
     * "Favorites" resource string used for tab title.
     */
    @BindString(R.string.tab_favorite)
    /* default */ String tabTitleFavorite;

    /**
     * "Settings" resource string used for tab title.
     */
    @BindString(R.string.tab_settings)
    /* default */ String tabTitleSettings;

    // endregion

    // region Private Fields

    /**
     * Listener used to change the selected item in the bottom navigation view when the page is switched.
     */
    private final OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            // ignore
        }

        @Override
        public void onPageSelected(final int position) {
            final int itemId;
            switch (position) {
                case 0:
                    itemId = R.id.navigation_home;
                    break;
                case 1:
                    itemId = R.id.navigation_favorites;
                    break;
                case 2:
                    itemId = R.id.navigation_settings;
                    break;
                default:
                    return;
            }

            bottomNavigationView.setSelectedItemId(itemId);
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            // ignore
        }
    };

    // endregion

    // region Private Inner Types

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        /* default */ SectionsPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            switch (position) {
                case 0:
                    return new NowPlayingMovieListFragment();
                case 1:
                    return new FavoriteMovieListFragment();
                case 2:
                    return new SettingsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return tabTitleNowPlaying;
                case 1:
                    return tabTitleFavorite;
                case 2:
                    return tabTitleSettings;
                default:
                    return null;
            }
        }
    }

    // endregion

    // region Public Overrides

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initNavigationView();
    }

    @Override
    public void onStop() {
        // Stop Glide to prevent memory leaks.
        // Use Glide.with(this).onDestroy() in onDestroy will cause exceptions.
        Glide.with(this).pauseRequests();
        super.onStop();
    }

    // endregion

    // region Private Methods

    private void initNavigationView() {
        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_favorites:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_settings:
                    viewPager.setCurrentItem(2);
                    return true;
                default:
                    return false;
            }
        });
    }

    // endregion
}
