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
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * AppCompatImageView with fixed aspect ratio (configured through "aspect_ratio" attribute).
 */
@SuppressWarnings("PMD.CommentRequired")
public class FixedAspectRatioImage extends AppCompatImageView {

    // region Private Fields

    private float aspectRatio;

    // endregion

    // region Constructors

    public FixedAspectRatioImage(final Context context) {
        super(context);
        init(context, null, 0);
    }

    public FixedAspectRatioImage(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public FixedAspectRatioImage(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    // endregion

    // region Public Methods

    public float getAspectRatio() {
        return aspectRatio;
    }

    // endregion

    // region Protected Overrides

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int measuredWidth = getMeasuredWidth();
        if (measuredWidth > 0 && aspectRatio > 0) {
            final int height = (int) (measuredWidth * aspectRatio);
            setMeasuredDimension(measuredWidth, height);
        }
    }

    // endregion

    // region Private Methods

    private void init(final Context context, final AttributeSet attrs, final int defStyle) {
        if (attrs != null) {
            final TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.FixedAspectRatioImage,
                    defStyle, 0);

            final int n = typeArray.getIndexCount();
            for (int i = 0; i < n; i++) {
                final int attr = typeArray.getIndex(i);
                if (attr == R.styleable.FixedAspectRatioImage_aspect_ratio) {
                    aspectRatio = typeArray.getFloat(
                            R.styleable.FixedAspectRatioImage_aspect_ratio, -1);
                }
            }

            typeArray.recycle();
        }
    }

    // endregion
}
