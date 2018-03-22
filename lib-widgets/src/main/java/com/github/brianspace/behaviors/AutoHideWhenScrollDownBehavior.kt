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

package com.github.brianspace.behaviors

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewCompat.NestedScrollType
import android.support.v4.view.ViewCompat.ScrollAxis
import android.util.AttributeSet
import android.view.View

/**
 * A CoordinatorLayout behavior for the target view to auto hide when scroll down.
 */
class AutoHideWhenScrollDownBehavior<V : View> : CoordinatorLayout.Behavior<V> {

    // region Private Properties

    /**
     * The current animator.
     */
    private var currentAnimator: Animator? = null

    /**
     * Flag for hiding the view.
     */
    private var isHiding: Boolean = false

    /**
     * Flag for showing the view.
     */
    private var isShowing: Boolean = false

    /**
     * Listener for events from hiding animator.
     */
    private val hidingAnimatorListener = object : AnimatorListener {
        override fun onAnimationStart(animator: Animator) {
            currentAnimator = animator
            isHiding = true
        }

        override fun onAnimationEnd(animator: Animator) {
            currentAnimator = null
            isHiding = false
        }

        override fun onAnimationCancel(animator: Animator) {
            currentAnimator = null
            isHiding = false
        }

        override fun onAnimationRepeat(animator: Animator) {
            // not used
        }
    }

    /**
     * Listener for events from showing animator.
     */
    private val showingAnimatorListener = object : AnimatorListener {
        override fun onAnimationStart(animator: Animator) {
            currentAnimator = animator
            isShowing = true
        }

        override fun onAnimationEnd(animator: Animator) {
            currentAnimator = null
            isShowing = false
        }

        override fun onAnimationCancel(animator: Animator) {
            currentAnimator = null
            isShowing = false
        }

        override fun onAnimationRepeat(animator: Animator) {
            // not used
        }
    }

    // endregion

    // region Constructors

    /**
     * Default constructor.
     */
    constructor() : super() {}

    /**
     * Constructor.
     * @param context Context.
     * @param attrs Attributes.
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    // endregion

    // region Public Overrides

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V, directTargetChild: View, target: View,
        @ScrollAxis axes: Int, @NestedScrollType type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V, target: View, dx: Int, dy: Int,
        consumed: IntArray, @NestedScrollType type: Int
    ) {
        if (dy < 0) {
            showView(child)
        } else if (dy > 0) {
            hideView(child)
        }
    }

    // endregion

    // region Private Methods

    private fun hideView(view: V) {
        if (isHiding) {
            return
        }

        if (isShowing && currentAnimator != null) {
            currentAnimator!!.cancel()
        }

        view.animate().translationY(view.height.toFloat()).setListener(hidingAnimatorListener)
    }

    private fun showView(view: V) {
        if (isShowing) {
            return
        }

        if (isHiding && currentAnimator != null) {
            currentAnimator!!.cancel()
        }

        view.animate().translationY(0f).setListener(showingAnimatorListener)
    }

    // endregion
}
