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

package com.github.brianspace.widgets;

import android.content.Context;
import android.support.v4.view.NestedScrollingChildHelper;
import android.util.AttributeSet;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;

/**
 * Extended SwipyRefreshLayout to support nested scroll.
 */
@SuppressWarnings("PMD.CommentRequired")
public class SwipeRefreshLayoutEx extends SwipyRefreshLayout {

    // region Private Fields

    private final NestedScrollingChildHelper childHelper = new NestedScrollingChildHelper(this);

    // endregion

    // region Constructors

    public SwipeRefreshLayoutEx(final Context context) {
        super(context);
    }

    public SwipeRefreshLayoutEx(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    // endregion

    // region Public Overrides

    @Override
    public void setNestedScrollingEnabled(final boolean enabled) {
        super.setNestedScrollingEnabled(enabled);
        childHelper.setNestedScrollingEnabled(enabled);
        setDistanceToTriggerSync(getResources().getDimensionPixelSize(R.dimen.default_trigger_distance));
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return childHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(final int axes) {
        return childHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        childHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return childHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(final int dxConsumed, final int dyConsumed, final int dxUnconsumed,
                                        final int dyUnconsumed, final int[] offsetInWindow) {
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(final int dx, final int dy, final int[] consumed,
            final int[] offsetInWindow) {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(final float velocityX, final float velocityY, final boolean consumed) {
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(final float velocityX, final float velocityY) {
        return childHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    // endregion
}
