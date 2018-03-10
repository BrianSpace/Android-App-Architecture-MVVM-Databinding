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

package com.github.brianspace.databinding.adapter;

import android.databinding.BindingAdapter;
import android.databinding.ObservableList;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * Databinding adapter for binding {@link android.databinding.ObservableList} to
 * {@link android.support.v7.widget.RecyclerView} with {@link RecyclerViewDatabindingAdapter}.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class RecyclerViewBindingAdapter {

    // region Constructors

    private RecyclerViewBindingAdapter() throws InstantiationException {
        throw new InstantiationException("Utility class RecyclerViewBindingAdapter should not be instantiated!");
    }

    // endregion

    // region Public Methods

    /**
     * Binding "messageSource" to display a message.
     *
     * @param view the RecyclerView which binds the item list.
     * @param itemList the list of items to be bound to the view.
     * @param itemBrId the data binding BR ID for the variable to be bound to the item.
     * @param itemLayoutId the layout ID for list items.
     */
    @BindingAdapter({"items", "itemBrId", "itemLayout"})
    public static <ItemTypeT> void setItems(final RecyclerView view, final ObservableList<ItemTypeT> itemList,
            final int itemBrId, @LayoutRes final int itemLayoutId) {
        if (itemList == null) {
            view.setAdapter(null);
        } else {
            final RecyclerViewDatabindingAdapter<ItemTypeT> adapter =
                    new RecyclerViewDatabindingAdapter<>(itemList, itemBrId, itemLayoutId);
            view.setAdapter(adapter);
        }
    }

    /**
     * Binding "messageSource" to display a message.
     *
     * @param view the RecyclerView which binds the item list.
     * @param itemList the list of items to be bound to the view.
     * @param itemBrId the data binding BR ID for the variable to be bound to the item.
     * @param itemLayoutId the layout ID for list items.
     * @param headerData the object bound to the header.
     * @param headerLayoutId the layout ID for list header.
     */
    @BindingAdapter({"items", "itemBrId", "itemLayout", "headerData", "headerBrId", "headerLayout"})
    public static <ItemTypeT> void setItemsAndHeader(final RecyclerView view, final ObservableList<ItemTypeT> itemList,
            final int itemBrId, @LayoutRes final int itemLayoutId,
            @NonNull final Object headerData, final int headerBrId, @LayoutRes final int headerLayoutId) {
        if (itemList == null) {
            view.setAdapter(null);
        } else {
            final HeaderedRecyclerViewDatabindingAdapter.HeaderParams headerParams =
                    new HeaderedRecyclerViewDatabindingAdapter.HeaderParams(headerLayoutId, headerBrId, headerData);
            final HeaderedRecyclerViewDatabindingAdapter<ItemTypeT> adapter =
                    new HeaderedRecyclerViewDatabindingAdapter<>(itemList, itemBrId, itemLayoutId, headerParams);
            view.setAdapter(adapter);
        }
    }

    // endregion
}