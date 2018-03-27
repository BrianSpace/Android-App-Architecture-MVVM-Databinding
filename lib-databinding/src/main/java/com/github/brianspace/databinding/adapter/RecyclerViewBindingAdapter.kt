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

import android.databinding.BindingAdapter
import android.databinding.ObservableList
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView

/**
 * Databinding adapter for binding [android.databinding.ObservableList] to
 * [android.support.v7.widget.RecyclerView] with [RecyclerViewDatabindingAdapter].
 */

/**
 * Binding "messageSource" to display a message.
 *
 * @param view the RecyclerView which binds the item list.
 * @param itemList the list of items to be bound to the view.
 * @param itemBrId the data binding BR ID for the variable to be bound to the item.
 * @param itemLayoutId the layout ID for list items.
 */
@BindingAdapter("items", "itemBrId", "itemLayout")
fun <ItemTypeT> setItems(
    view: RecyclerView, itemList: ObservableList<ItemTypeT>?,
    itemBrId: Int, @LayoutRes itemLayoutId: Int
) {
    if (itemList == null) {
        view.adapter = null
    } else {
        val adapter = RecyclerViewDatabindingAdapter(itemList, itemBrId, itemLayoutId)
        view.adapter = adapter
    }
}

/**
 * Binding "messageSource" to display a message.
 *
 * @param view the RecyclerView which binds the item list.
 * @param itemList the list of items to be bound to the view.
 * @param itemBrId the data binding BR ID for the variable to be bound to the item.
 * @param itemLayoutId the layout ID for list items.
 * @param headerParams parameters for the header.
 */
@BindingAdapter("items", "itemBrId", "itemLayout", "headerParams")
fun <ItemTypeT> setItemsAndHeader(
    view: RecyclerView, itemList: ObservableList<ItemTypeT>?,
    itemBrId: Int, @LayoutRes itemLayoutId: Int,
    headerParams: HeaderedRecyclerViewDatabindingAdapter.HeaderParams
) {
    if (itemList == null) {
        view.adapter = null
    } else {
        val adapter = HeaderedRecyclerViewDatabindingAdapter(itemList, itemBrId, itemLayoutId, headerParams)
        view.adapter = adapter
    }
}
