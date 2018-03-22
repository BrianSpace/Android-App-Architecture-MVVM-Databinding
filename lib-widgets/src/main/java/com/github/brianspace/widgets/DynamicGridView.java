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
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * Grid view that can adjust number of columns based on cell width.
 * Configured through "min_cell_width" and "cell_spacing" attribute.
 * CAUTION: assume header view type will not be 0.
 * TODO: fix the alignment after item add/remove caused by assuming fixed item decorations during item animations.
 */
public class DynamicGridView extends RecyclerView {

    // region Private Fields

    /**
     * Column count for the grid.
     */
    private int columnCount;

    /**
     * Flag to indicate that the item decorations need to be updated.
     */
    private boolean needUpdateItemDecoration;

    /**
     * Flag of active state.
     */
    private boolean isActive;

    // endregion

    // region Private Inner Types

    /**
     * Extends ItemDecoration to calculate the offset of the items from the default grid edges.
     */
    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        /**
         * Desired space between items.
         */
        private final int space;
        /**
         * Store item offsets, from start to end.
         */
        private final int[][] offsets;

        /**
         * Constructor.
         *
         * @param space                 desired space between items.
         * @param leftAndRightPadding   padding on the grid's left and right.
         * @param columnWidth           width of the column.
         * @param contentWidth          width of the content to be displayed.
         */
        public SpacesItemDecoration(final int space, final int leftAndRightPadding,
                final int columnWidth, final int contentWidth) {
            this.space = space;
            final int totalPadding = columnWidth - contentWidth;
            offsets = new int[columnCount][2];
            offsets[0][0] = leftAndRightPadding + space;
            for (int col = 0; col < columnCount - 1; ++col) {
                final int offsetRight = totalPadding - offsets[col][0];
                offsets[col][1] = offsetRight;
                offsets[col + 1][0] = space - offsetRight;
            }

            offsets[columnCount - 1][1] = offsets[0][0];
        }

        @Override
        public void getItemOffsets(final Rect outsideRect, final View view, final RecyclerView parent,
                final RecyclerView.State state) {
            // Calculate header count.
            // Since headers may change at runtime so need to re-calculate every time.
            // CAUTION: assume header view type will not be 0.
            final int headerCount = getHeaderCount(parent);

            int position = parent.getChildLayoutPosition(view);

            // No offset for headers.
            if (position < headerCount) {
                outsideRect.top = 0;
                outsideRect.left = 0;
                outsideRect.right = 0;
                outsideRect.bottom = 0;
                return;
            }

            position -= headerCount;
            final int row = position / columnCount; // item row
            final int column = position % columnCount; // item column

            // Add top margin only for the first row to avoid double space between items
            if (row == 0) {
                outsideRect.top = space;
            } else {
                outsideRect.top = 0;
            }

            outsideRect.bottom = space;
            outsideRect.left = offsets[column][0];
            outsideRect.right = offsets[column][1];
        }

        private int getHeaderCount(final RecyclerView recyclerView) {
            final Adapter adapter = recyclerView.getAdapter();
            int headerCount = 0;
            for (int index = 0; index < adapter.getItemCount(); ++index) {
                if (adapter.getItemViewType(index) == 0) {
                    break;
                } else {
                    ++headerCount;
                }
            }

            return headerCount;
        }
    }

    /**
     * Observer for adapter data. Used to invalidate item decorations if needed.
     */
    private final AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            updateItemDecoration();
        }

        @Override
        public void onItemRangeInserted(final int positionStart, final int itemCount) {
            // Invalidate if the items are not appended at the end.
            if (getAdapter().getItemCount() != positionStart + itemCount) {
                updateItemDecoration();
            }
        }

        @Override
        public void onItemRangeRemoved(final int positionStart, final int itemCount) {
            // Invalidate if the items are not removed at the end.
            if (getAdapter().getItemCount() != positionStart) {
                updateItemDecoration();
            }
        }

        @Override
        public void onItemRangeMoved(final int fromPosition, final int toPosition, final int itemCount) {
            updateItemDecoration();
        }

        private void updateItemDecoration() {
            Log.d("DynamicGridView", "updateItemDecoration: isActive = " + isActive);
            if (isActive) {
                invalidateItemDecorations();
                getLayoutManager().requestLayout();
            } else {
                needUpdateItemDecoration = true;
            }
        }
    };

    // endregion

    // region Constructors

    /**
     * Constructor.
     * @param context Context.
     */
    public DynamicGridView(final Context context) {
        super(context);
        init(context, null, 0);
    }

    /**
     * Constructor.
     * @param context Context.
     * @param attrs Attributes.
     */
    public DynamicGridView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    /**
     * Constructor.
     * @param context Context.
     * @param attrs Attributes.
     * @param defStyle Style attribute.
     */
    public DynamicGridView(final Context context, @Nullable final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    // endregion

    // region Public Overrides

    @Override
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public void setAdapter(@Nullable final Adapter adapter) {
        final Adapter currentAdapter = getAdapter();
        if (currentAdapter != null && currentAdapter != adapter) { // Compare object, not equals.
            currentAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        }

        super.setAdapter(adapter);
        if (adapter == null) {
            return;
        }

        adapter.registerAdapterDataObserver(adapterDataObserver);
    }

    // endregion

    // region Protected Overrides

    @Override
    protected void onFocusChanged(final boolean gainFocus, final int direction,
            @Nullable final Rect previouslyFocusedRect) {
        Log.d("DynamicGridView", "onFocusChanged: gainFocus = " + gainFocus);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        isActive = gainFocus;
        if (gainFocus && needUpdateItemDecoration) {
            Log.d("DynamicGridView", "onFocusChanged: invalidateItemDecoration");
            invalidateItemDecorations();
            needUpdateItemDecoration = false;
        }
    }

    // endregion

    // region Private Methods

    private void init(final Context context, @Nullable final AttributeSet attrs, final int defStyle) {
        setHasFixedSize(true);
        int minCellWidth = getResources().getDimensionPixelSize(R.dimen.default_grid_width);
        int spacing = getResources().getDimensionPixelSize(R.dimen.default_grid_spacing);

        if (attrs != null) {
            final TypedArray typeArray = context.obtainStyledAttributes(attrs,
                    R.styleable.DynamicGridView, defStyle, 0);

            final int n = typeArray.getIndexCount();
            for (int i = 0; i < n; i++) {
                final int attr = typeArray.getIndex(i);
                if (attr == R.styleable.DynamicGridView_min_cell_width) {
                    minCellWidth = typeArray.getDimensionPixelSize(R.styleable.DynamicGridView_min_cell_width,
                            minCellWidth);
                } else if (attr == R.styleable.DynamicGridView_cell_spacing) {
                    spacing = typeArray.getDimensionPixelSize(
                            R.styleable.DynamicGridView_cell_spacing, spacing);
                }
            }

            typeArray.recycle();
        }

        initColumns(minCellWidth, spacing);
    }

    private void initColumns(final int minCellWidth, final int spacing) {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int width = metrics.widthPixels;
        final int realSpacing = ((spacing + 1) / 2) * 2; // Make spacing the multiple of 2.
        final int minColumnCount = 1; // To avoid PMD.AvoidLiteralsInIfCondition
        columnCount = width / minCellWidth;
        if (columnCount <= minColumnCount) {
            columnCount = minColumnCount;
            setLayoutManager(new LinearLayoutManager(getContext()));
            addItemDecoration(new SpacesItemDecoration(realSpacing, 0, width, width - 2 * realSpacing));
        } else {
            final int columnWidth = width / columnCount;
            final int totalSpacing = realSpacing * (columnCount + 1);
            int gridContentWidth = (width - totalSpacing) / columnCount;
            gridContentWidth = (gridContentWidth / 2) * 2;  // Make content width also the multiple of 2.
            final int leftAndRightPadding = (width - gridContentWidth * columnCount - totalSpacing) / 2;
            setLayoutManager(new GridLayoutManager(getContext(), columnCount));
            addItemDecoration(
                    new SpacesItemDecoration(realSpacing, leftAndRightPadding, columnWidth, gridContentWidth));
        }
    }

    // endregion
}