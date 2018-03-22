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
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

/**
 * Grid view that can adjust number of columns based on cell width.
 * Configured through "min_cell_width" and "cell_spacing" attribute.
 * CAUTION: assume header view type will not be 0.
 * TODO: fix the alignment after item add/remove caused by assuming fixed item decorations during item animations.
 */
class DynamicGridView : RecyclerView {

    // region Private Fields

    /**
     * Column count for the grid.
     */
    private var columnCount: Int = 0

    /**
     * Flag to indicate that the item decorations need to be updated.
     */
    private var needUpdateItemDecoration: Boolean = false

    /**
     * Flag of active state.
     */
    private var isActive: Boolean = false

    // endregion

    // region Private Inner Types

    /**
     * Extends ItemDecoration to calculate the offset of the items from the default grid edges.
     */
    private inner class SpacesItemDecoration
    /**
     * Constructor.
     *
     * @param space                 desired space between items.
     * @param leftAndRightPadding   padding on the grid's left and right.
     * @param columnWidth           width of the column.
     * @param contentWidth          width of the content to be displayed.
     */
        (
        private val space: Int, leftAndRightPadding: Int,
        columnWidth: Int, contentWidth: Int
    ) : RecyclerView.ItemDecoration() {
        /**
         * Store item offsets, from start to end.
         */
        private val offsets: Array<IntArray>

        init {
            val totalPadding = columnWidth - contentWidth
            offsets = Array(columnCount) { IntArray(2) }
            offsets[0][0] = leftAndRightPadding + space
            for (col in 0 until columnCount - 1) {
                val offsetRight = totalPadding - offsets[col][0]
                offsets[col][1] = offsetRight
                offsets[col + 1][0] = space - offsetRight
            }

            offsets[columnCount - 1][1] = offsets[0][0]
        }

        override fun getItemOffsets(
            outsideRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State?
        ) {
            // Calculate header count.
            // Since headers may change at runtime so need to re-calculate every time.
            // CAUTION: assume header view type will not be 0.
            val headerCount = getHeaderCount(parent)

            var position = parent.getChildLayoutPosition(view)

            // No offset for headers.
            if (position < headerCount) {
                outsideRect.apply {
                    top = 0
                    left = 0
                    right = 0
                    bottom = 0
                }
                return
            }

            position -= headerCount
            val row = position / columnCount // item row
            val column = position % columnCount // item column

            // Add top margin only for the first row to avoid double space between items
            if (row == 0) {
                outsideRect.top = space
            } else {
                outsideRect.top = 0
            }

            outsideRect.apply {
                bottom = space
                left = offsets[column][0]
                right = offsets[column][1]
            }
        }

        private fun getHeaderCount(recyclerView: RecyclerView): Int {
            val adapter = recyclerView.adapter
            var headerCount = 0
            for (index in 0 until adapter.itemCount) {
                if (adapter.getItemViewType(index) == 0) {
                    break
                } else {
                    ++headerCount
                }
            }

            return headerCount
        }
    }

    /**
     * Observer for adapter data. Used to invalidate item decorations if needed.
     */
    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            updateItemDecoration()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            // Invalidate if the items are not appended at the end.
            if (getAdapter().getItemCount() != positionStart + itemCount) {
                updateItemDecoration()
            }
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            // Invalidate if the items are not removed at the end.
            if (getAdapter().getItemCount() != positionStart) {
                updateItemDecoration()
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            updateItemDecoration()
        }

        private fun updateItemDecoration() {
            if (isActive) {
                invalidateItemDecorations()
                getLayoutManager().requestLayout()
            } else {
                needUpdateItemDecoration = true
            }
        }
    }

    // endregion

    // region Constructors

    /**
     * Constructor.
     * @param context Context.
     */
    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    /**
     * Constructor.
     * @param context Context.
     * @param attrs Attributes.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    /**
     * Constructor.
     * @param context Context.
     * @param attrs Attributes.
     * @param defStyle Style attribute.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    // endregion

    // region Public Overrides

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        val currentAdapter = getAdapter()
        if (currentAdapter != null && currentAdapter !== adapter) { // Compare object, not content.
            currentAdapter.unregisterAdapterDataObserver(adapterDataObserver)
        }

        super.setAdapter(adapter)
        if (adapter == null) {
            return
        }

        adapter.registerAdapterDataObserver(adapterDataObserver)
    }

    // endregion

    // region Protected Overrides

    protected override fun onFocusChanged(
        gainFocus: Boolean, direction: Int,
        previouslyFocusedRect: Rect?
    ) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        isActive = gainFocus
        if (gainFocus && needUpdateItemDecoration) {
            invalidateItemDecorations()
            needUpdateItemDecoration = false
        }
    }

    // endregion

    // region Private Methods

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        setHasFixedSize(true)
        var minCellWidth = resources.getDimensionPixelSize(R.dimen.default_grid_width)
        var spacing = resources.getDimensionPixelSize(R.dimen.default_grid_spacing)

        if (attrs != null) {
            val typeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.DynamicGridView, defStyle, 0
            )

            val n = typeArray.indexCount
            for (i in 0 until n) {
                val attr = typeArray.getIndex(i)
                if (attr == R.styleable.DynamicGridView_min_cell_width) {
                    minCellWidth = typeArray.getDimensionPixelSize(
                        R.styleable.DynamicGridView_min_cell_width,
                        minCellWidth
                    )
                } else if (attr == R.styleable.DynamicGridView_cell_spacing) {
                    spacing = typeArray.getDimensionPixelSize(
                        R.styleable.DynamicGridView_cell_spacing, spacing
                    )
                }
            }

            typeArray.recycle()
        }

        initColumns(minCellWidth, spacing)
    }

    private fun initColumns(minCellWidth: Int, spacing: Int) {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val realSpacing = (spacing + 1) / 2 * 2 // Make spacing the multiple of 2.
        columnCount = width / minCellWidth
        if (columnCount <= 1) { // Minimum: one column
            columnCount = 1
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpacesItemDecoration(realSpacing, 0, width, width - 2 * realSpacing))
        } else {
            val columnWidth = width / columnCount
            val totalSpacing = realSpacing * (columnCount + 1)
            var gridContentWidth = (width - totalSpacing) / columnCount
            gridContentWidth = gridContentWidth / 2 * 2  // Make content width also the multiple of 2.
            val leftAndRightPadding = (width - gridContentWidth * columnCount - totalSpacing) / 2
            layoutManager = GridLayoutManager(context, columnCount)
            addItemDecoration(
                SpacesItemDecoration(realSpacing, leftAndRightPadding, columnWidth, gridContentWidth)
            )
        }
    }

    // endregion
}