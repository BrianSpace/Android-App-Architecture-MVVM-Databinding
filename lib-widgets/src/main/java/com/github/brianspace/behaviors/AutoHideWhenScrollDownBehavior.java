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

package com.github.brianspace.behaviors;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewCompat.NestedScrollType;
import android.support.v4.view.ViewCompat.ScrollAxis;
import android.util.AttributeSet;
import android.view.View;

/**
 * A CoordinatorLayout behavior for the target view to auto hide when scroll down.
 */
public class AutoHideWhenScrollDownBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    // region Private Fields

    /**
     * The current animator.
     */
    private Animator currentAnimator;

    /**
     * Flag for hiding the view.
     */
    private boolean isHiding;

    /**
     * Flag for showing the view.
     */
    private boolean isShowing;

    /**
     * Listener for events from hiding animator.
     */
    private final AnimatorListener hidingAnimatorListener = new AnimatorListener() {
        @Override
        public void onAnimationStart(final Animator animator) {
            currentAnimator = animator;
            isHiding = true;
        }

        @Override
        public void onAnimationEnd(final Animator animator) {
            currentAnimator = null;
            isHiding = false;
        }

        @Override
        public void onAnimationCancel(final Animator animator) {
            currentAnimator = null;
            isHiding = false;
        }

        @Override
        public void onAnimationRepeat(final Animator animator) {
            // not used
        }
    };

    /**
     * Listener for events from showing animator.
     */
    private final AnimatorListener showingAnimatorListener = new AnimatorListener() {
        @Override
        public void onAnimationStart(final Animator animator) {
            currentAnimator = animator;
            isShowing = true;
        }

        @Override
        public void onAnimationEnd(final Animator animator) {
            currentAnimator = null;
            isShowing = false;
        }

        @Override
        public void onAnimationCancel(final Animator animator) {
            currentAnimator = null;
            isShowing = false;
        }

        @Override
        public void onAnimationRepeat(final Animator animator) {
            // not used
        }
    };

    // endregion

    // region Constructors

    /**
     * Default constructor.
     */
    public AutoHideWhenScrollDownBehavior() {
        super();
    }

    /**
     * Constructor.
     * @param context Context.
     * @param attrs Attributes.
     */
    public AutoHideWhenScrollDownBehavior(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    // endregion

    // region Public Overrides

    @Override
    public boolean onStartNestedScroll(@NonNull final CoordinatorLayout coordinatorLayout,
            @NonNull final V child, @NonNull final View directTargetChild, @NonNull final View target,
            @ScrollAxis final int axes, @NestedScrollType final int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(@NonNull final CoordinatorLayout coordinatorLayout,
            @NonNull final V child, @NonNull final View target, final int dx, final int dy,
            @NonNull final int[] consumed, @NestedScrollType final int type) {
        if (dy < 0) {
            showView(child);
        } else if (dy > 0) {
            hideView(child);
        }
    }

    // endregion

    // region Private Methods

    private void hideView(final V view) {
        if (isHiding) {
            return;
        }

        if (isShowing && currentAnimator != null) {
            currentAnimator.cancel();
        }

        view.animate().translationY(view.getHeight()).setListener(hidingAnimatorListener);
    }

    private void showView(final V view) {
        if (isShowing) {
            return;
        }

        if (isHiding && currentAnimator != null) {
            currentAnimator.cancel();
        }

        view.animate().translationY(0).setListener(showingAnimatorListener);
    }

    // endregion
}
