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
import android.os.Handler
import android.os.Looper
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList

/**
 * [android.support.v7.widget.RecyclerView] adapter supporting data binding to
 * [android.databinding.ObservableList].
 *
 * @param <ItemTypeT> type of the items in the [android.databinding.ObservableList].
 */
open class RecyclerViewDatabindingAdapter<ItemTypeT>(
    /**
     * The list of items for binding.
     */
    private val itemList: ObservableList<ItemTypeT>,
    /**
     * Data binding ID (BR.*) for the &lt;data&gt; element used for binding in the view's layout xml.
     */
    private val itemDataBrId: Int,
    /**
     * The layout ID used for inflating the item views.
     */
    @param:LayoutRes @field:LayoutRes
    private val itemLayoutId: Int
) : RecyclerView.Adapter<RecyclerViewDatabindingAdapter.BindingHolder>() {

    // region Private/Protected Properties

    /**
     * The internal list of items for the adapter, to avoid the crash caused by inconsistency.
     */
    protected val adapterItems: MutableList<ItemTypeT> = ArrayList()

    /**
     * The callback for the list changed events. Use Handler of the main looper to avoid the exception:
     * IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling
     */
    private val listChangedCallback = object : ObservableList.OnListChangedCallback<ObservableList<ItemTypeT>>() {

        /**
         * Handler of the main looper.
         */
        private val mainHandler = Handler(Looper.getMainLooper())

        override fun onChanged(items: ObservableList<ItemTypeT>) {
            onItemChanged(items)
        }

        override fun onItemRangeChanged(
            items: ObservableList<ItemTypeT>,
            start: Int, count: Int
        ) {
            mainHandler.post {
                for (i in start until start + count) {
                    adapterItems[i] = items[i]
                }

                notifyItemRangeChanged(getItemLayoutPosition(start), count)
            }
        }

        override fun onItemRangeInserted(
            items: ObservableList<ItemTypeT>,
            start: Int, count: Int
        ) {
            mainHandler.post {
                for (i in start + count - 1 downTo start) {
                    adapterItems.add(start, items[i])
                }

                notifyItemRangeInserted(getItemLayoutPosition(start), count)
            }
        }

        override fun onItemRangeMoved(
            items: ObservableList<ItemTypeT>,
            start: Int, to: Int, count: Int
        ) {
            onItemChanged(items)
        }

        override fun onItemRangeRemoved(
            items: ObservableList<ItemTypeT>,
            start: Int, count: Int
        ) {
            mainHandler.post {
                for (i in 0 until count) {
                    adapterItems.removeAt(start)
                }

                notifyItemRangeRemoved(getItemLayoutPosition(start), count)
            }
        }

        private fun onItemChanged(items: ObservableList<ItemTypeT>) {
            mainHandler.post {
                adapterItems.clear()
                adapterItems.addAll(items)
                notifyDataSetChanged()
            }
        }
    }

    // endregion

    // region Public Inner Types

    /**
     * View holder for the items.
     * @param itemView view for the list item.
     */
    class BindingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Binding for the view.
         */
        var binding: ViewDataBinding? = null
    }

    // endregion

    // region Public Overrides

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val itemBinding =
            DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), itemLayoutId, parent, false)
        return BindingHolder(itemBinding.root).apply {
            binding = itemBinding
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerViewDatabindingAdapter.BindingHolder,
        position: Int
    ) {
        val item = getItemForBinding(position)
        holder.binding?.apply {
            setVariable(itemDataBrId, item)
            executePendingBindings()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        if (!itemList.isEmpty()) {
            listChangedCallback.onChanged(itemList)
        }

        this.itemList.addOnListChangedCallback(listChangedCallback)
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        this.itemList.removeOnListChangedCallback(listChangedCallback)
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int {
        return adapterItems.size
    }

    // endregion

    // region Protected Methods

    /**
     * Get the item for binding.
     * Headered list should override this.
     *
     * @param position item position.
     * @return the item object at the position.
     */
    protected open fun getItemForBinding(position: Int): Any? {
        return adapterItems[position]
    }

    /**
     * Get the layout position of the item.
     * Headered list should override this.
     *
     * @param position the position of item in the item list.
     * @return the corresponding layout position in the recycler view.
     */
    protected open fun getItemLayoutPosition(position: Int): Int {
        return position
    }

    // endregion
}
