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

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * {@link android.support.v7.widget.RecyclerView} adapter supporting data binding to
 * {@link android.databinding.ObservableList} with a header which bind to a different object outside the list of items.
 *
 * @param <ItemTypeT> type of the items in the {@link android.databinding.ObservableList}.
 */
public class HeaderedRecyclerViewDatabindingAdapter<ItemTypeT>
        extends RecyclerViewDatabindingAdapter<ItemTypeT> {

    // region Private Constants

    /**
     * Item type for normal list item.
     */
    private static final int TYPE_ITEM = 0;
    /**
     * Item type for list header.
     */
    private static final int TYPE_HEADER = 1;

    // endregion

    // region Private Fields

    /**
     * The header parameters.
     */
    private final HeaderParams headerParams;

    // endregion

    // region Public Inner Types

    /**
     * Parameters for the list header.
     */
    public static class HeaderParams {
        /**
         * The layout ID used for inflating the header view.
         */
        @LayoutRes
        public final int headerLayoutId;
        /**
         * Data binding ID (BR.*) for the &lt;data&gt; element used for binding in the header's layout xml.
         */
        public final int headerDataBrId;
        /**
         * The data for binding in the header.
         */
        public final Object headerData;

        /**
         * Constructor.
         * @param headerLayoutId The layout ID used for inflating the header view.
         * @param headerDataBrId Data binding ID (BR.*) for the &lt;data&gt; element used for the header.
         * @param headerData value to bind.
         */
        public HeaderParams(@LayoutRes final int headerLayoutId, final int headerDataBrId,
                @NonNull final Object headerData) {
            this.headerLayoutId = headerLayoutId;
            this.headerDataBrId = headerDataBrId;
            this.headerData = headerData;
        }
    }

    // endregion

    // region Constructors

    /**
     * Constructor for HeaderedRecyclerViewDatabindingAdapter.
     *
     * @param itemList          Observable item list.
     * @param itemDataBrId      Variable name in &lt;data&gt; element used for binding in the item layout.
     * @param itemLayoutId      Layout ID for list item.
     * @param headerParams      Parameters for the list header.
     */
    public HeaderedRecyclerViewDatabindingAdapter(@NonNull final ObservableList<ItemTypeT> itemList,
            final int itemDataBrId, @LayoutRes final int itemLayoutId, @NonNull final HeaderParams headerParams) {
        super(itemList, itemDataBrId, itemLayoutId);
        this.headerParams = headerParams;
    }

    // endregion

    // region Public Overrides

    @Override
    public int getItemViewType(final int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public BindingHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == TYPE_HEADER) {
            final ViewDataBinding headerBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                            headerParams.headerLayoutId, parent, false);

            final BindingHolder holder = new BindingHolder(headerBinding.getRoot());
            holder.setBinding(headerBinding);

            if (parent instanceof RecyclerView) {
                prepareHeaderLayout((RecyclerView) parent, holder);
            }

            return holder;
        }

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewDatabindingAdapter<ItemTypeT>.BindingHolder holder,
                                 final int position) {
        if (position == 0) {
            final ViewDataBinding itemBinding = holder.getBinding();
            itemBinding.setVariable(headerParams.headerDataBrId, headerParams.headerData);
            itemBinding.executePendingBindings();
            return;
        }

        super.onBindViewHolder(holder, position);
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(final int i) {
                    return getItemViewType(i) == TYPE_HEADER ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }

        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return adapterItems.size() + 1;
    }

    // endregion

    // region Protected Overrides

    @Override
    protected Object getItemForBinding(final int position) {
        return position == 0 ? headerParams.headerData : adapterItems.get(position - 1);
    }

    @Override
    protected int getItemLayoutPosition(final int position) {
        return position + 1;
    }

    // endregion

    // region Private Methods

    private void prepareHeaderLayout(final RecyclerView parentRecyclerView, final BindingHolder holder) {
        final RecyclerView.LayoutManager layoutManager = parentRecyclerView.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            holder.itemView.setLayoutParams(layoutParams);
        }
    }

    // endregion
}
