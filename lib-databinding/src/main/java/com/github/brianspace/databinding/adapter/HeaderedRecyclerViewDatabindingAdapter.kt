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

package com.github.brianspace.databinding.adapter

import android.databinding.DataBindingUtil
import android.databinding.ObservableList
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup

// region Private Constants

/**
 * Item type for normal list item.
 */
private const val TYPE_ITEM = 0
/**
 * Item type for list header.
 */
private const val TYPE_HEADER = 1

// endregion

/**
 * [android.support.v7.widget.RecyclerView] adapter supporting data binding to
 * [android.databinding.ObservableList] with a header which bind to a different object outside the list of items.
 *
 * @param <ItemTypeT> type of the items in the [android.databinding.ObservableList].
 */
open class HeaderedRecyclerViewDatabindingAdapter<ItemTypeT>(
    /**
     * Observable item list.
     */
    itemList: ObservableList<ItemTypeT>,
    /**
     * Variable name in &lt;data&gt; element used for binding in the item layout.
     */
    itemDataBrId: Int,
    /**
     * Layout ID for list item.
     */
    @LayoutRes itemLayoutId: Int,
    /**
     * Parameters for the list header.
     */
    private val headerParams: HeaderParams
) : RecyclerViewDatabindingAdapter<ItemTypeT>(itemList, itemDataBrId, itemLayoutId) {

    // region Public Inner Types

    /**
     * Parameters for the list header.
     */
    class HeaderParams(
        /**
         * The layout ID used for inflating the header view.
         */
        @param:LayoutRes @field:LayoutRes
        val headerLayoutId: Int,
        /**
         * Data binding ID (BR.*) for the &lt;data&gt; element used for binding in the header's layout xml.
         */
        val headerDataBrId: Int,
        /**
         * The data for binding in the header.
         */
        val headerData: Any
    )

    // endregion

    // region Public Overrides

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewDatabindingAdapter.BindingHolder {
        if (viewType == TYPE_HEADER) {
            val headerBinding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(parent.context),
                headerParams.headerLayoutId, parent, false
            )

            return BindingHolder(headerBinding.root).apply {
                binding = headerBinding
                if (parent is RecyclerView) {
                    prepareHeaderLayout(parent, this)
                }
            }
        }

        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(
        holder: RecyclerViewDatabindingAdapter.BindingHolder,
        position: Int
    ) {
        if (position == 0) {
            holder.binding?.apply {
                setVariable(headerParams.headerDataBrId, headerParams.headerData)
                executePendingBindings()
            }
            return
        }

        super.onBindViewHolder(holder, position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(i: Int): Int {
                    return if (getItemViewType(i) == TYPE_HEADER) layoutManager.spanCount else 1
                }
            }
        }

        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int {
        return adapterItems.size + 1
    }

    // endregion

    // region Protected Overrides

    override fun getItemForBinding(position: Int): Any? {
        return if (position == 0) headerParams.headerData else adapterItems[position - 1]
    }

    override fun getItemLayoutPosition(position: Int): Int {
        return position + 1
    }

    // endregion

    // region Private Methods

    private fun prepareHeaderLayout(
        parentRecyclerView: RecyclerView,
        holder: RecyclerViewDatabindingAdapter.BindingHolder
    ) {
        if (parentRecyclerView.layoutManager is StaggeredGridLayoutManager) {
            holder.itemView.layoutParams = StaggeredGridLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { isFullSpan = true }
        }
    }
}
