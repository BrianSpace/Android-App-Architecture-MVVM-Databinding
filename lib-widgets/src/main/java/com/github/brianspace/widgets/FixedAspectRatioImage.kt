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

package com.github.brianspace.widgets

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

/**
 * AppCompatImageView with fixed aspect ratio (configured through "aspect_ratio" attribute).
 */
class FixedAspectRatioImage : AppCompatImageView {

    // region Public Methods

    var aspectRatio: Float = 0.0f
        private set

    // endregion

    // region Constructors

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    // endregion

    // region Protected Overrides

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth
        if (measuredWidth > 0 && aspectRatio > 0) {
            val height = (measuredWidth * aspectRatio).toInt()
            setMeasuredDimension(measuredWidth, height)
        }
    }

    // endregion

    // region Private Methods

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        if (attrs != null) {
            val typeArray = context.obtainStyledAttributes(
                attrs, R.styleable.FixedAspectRatioImage,
                defStyle, 0
            )

            val n = typeArray.indexCount
            for (i in 0 until n) {
                val attr = typeArray.getIndex(i)
                if (attr == R.styleable.FixedAspectRatioImage_aspect_ratio) {
                    aspectRatio = typeArray.getFloat(
                        R.styleable.FixedAspectRatioImage_aspect_ratio, -1f
                    )
                }
            }

            typeArray.recycle()
        }
    }

    // endregion
}
