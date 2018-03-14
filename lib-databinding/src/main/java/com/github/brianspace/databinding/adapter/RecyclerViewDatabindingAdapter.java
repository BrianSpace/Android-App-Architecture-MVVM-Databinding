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
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link android.support.v7.widget.RecyclerView} adapter supporting data binding to
 * {@link android.databinding.ObservableList}.
 *
 * @param <ItemTypeT> type of the items in the {@link android.databinding.ObservableList}.
 */
public class RecyclerViewDatabindingAdapter<ItemTypeT>
        extends RecyclerView.Adapter<RecyclerViewDatabindingAdapter<ItemTypeT>.BindingHolder> {

    // region Private Fields

    /**
     * The list of items for binding.
     */
    private final ObservableList<ItemTypeT> itemList;

    /**
     * The internal list of items for the adapter, to avoid the crash caused by inconsistency.
     */
    @SuppressWarnings("WeakerAccess")
    protected final List<ItemTypeT> adapterItems = new ArrayList<>();

    /**
     * The layout ID used for inflating the item views.
     */
    @LayoutRes
    private final int itemLayoutId;
    /**
     * Data binding ID (BR.*) for the &lt;data&gt; element used for binding in the view's layout xml.
     */
    private final int itemDataBrId;

    /**
     * The callback for the list changed events. Use Handler of the main looper to avoid the exception:
     *     IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling
     */
    private final ObservableList.OnListChangedCallback<ObservableList<ItemTypeT>> listChangedCallback =
            new ObservableList.OnListChangedCallback<ObservableList<ItemTypeT>>() {

                /**
                 * Handler of the main looper.
                 */
                private final Handler mainHandler = new Handler(Looper.getMainLooper());

                @Override
                public void onChanged(final ObservableList<ItemTypeT> items) {
                    onItemChanged(items);
                }

                @Override
                public void onItemRangeChanged(final ObservableList<ItemTypeT> items,
                        final int start, final int count) {
                    mainHandler.post(() -> {
                        for (int i = start; i < start + count; ++i) {
                            adapterItems.set(i, items.get(i));
                        }

                        notifyItemRangeChanged(getItemLayoutPosition(start), count);
                    });
                }

                @Override
                public void onItemRangeInserted(final ObservableList<ItemTypeT> items,
                        final int start, final int count) {
                    mainHandler.post(() -> {
                        for (int i = start + count - 1; i >= start; --i) {
                            adapterItems.add(start, items.get(i));
                        }

                        notifyItemRangeInserted(getItemLayoutPosition(start), count);
                    });
                }

                @Override
                public void onItemRangeMoved(final ObservableList<ItemTypeT> items,
                        final int start, final int to, final int count) {
                    onItemChanged(items);
                }

                @Override
                public void onItemRangeRemoved(final ObservableList<ItemTypeT> items,
                        final int start, final int count) {
                    mainHandler.post(() -> {
                        for (int i = 0; i < count; ++i) {
                            adapterItems.remove(start);
                        }

                        notifyItemRangeRemoved(getItemLayoutPosition(start), count);
                    });
                }

                private void onItemChanged(final ObservableList<ItemTypeT> items) {
                    mainHandler.post(() -> {
                        adapterItems.clear();
                        adapterItems.addAll(items);
                        notifyDataSetChanged();
                    });
                }
            };

    // endregion

    // region Public Inner Types

    /**
     * View holder for the items.
     */
    public class BindingHolder extends RecyclerView.ViewHolder {

        /**
         * Binding for the view.
         */
        private ViewDataBinding binding;

        /**
         * Constructor.
         *
         * @param itemView view for the list item.
         */
        public BindingHolder(@NonNull final View itemView) {
            super(itemView);
        }

        /**
         * Get the binding.
         */
        public ViewDataBinding getBinding() {
            return binding;
        }

        /**
         * Set the binding.
         */
        public void setBinding(final ViewDataBinding binding) {
            this.binding = binding;
        }
    }

    // endregion

    // region Constructors

    /**
     * Constructor for RecyclerViewDatabindingAdapter.
     *
     * @param itemList Observable item list.
     * @param itemDataBrId Variable name in &lt;data&gt; element used for binding in the item layout.
     * @param itemLayoutId Layout ID for list item.
     */
    public RecyclerViewDatabindingAdapter(@NonNull final ObservableList<ItemTypeT> itemList, final int itemDataBrId,
            @LayoutRes final int itemLayoutId) {
        this.itemList = itemList;
        this.itemDataBrId = itemDataBrId;
        this.itemLayoutId = itemLayoutId;
    }

    // endregion

    // region Public Overrides

    @Override
    public BindingHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final ViewDataBinding itemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemLayoutId, parent, false);
        final BindingHolder holder = new BindingHolder(itemBinding.getRoot());
        holder.setBinding(itemBinding);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewDatabindingAdapter<ItemTypeT>.BindingHolder holder,
            final int position) {
        final Object item = getItemForBinding(position);
        final ViewDataBinding itemBinding = holder.getBinding();
        itemBinding.setVariable(itemDataBrId, item);
        itemBinding.executePendingBindings();
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        if (!itemList.isEmpty()) {
            listChangedCallback.onChanged(itemList);
        }

        this.itemList.addOnListChangedCallback(listChangedCallback);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        this.itemList.removeOnListChangedCallback(listChangedCallback);
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return adapterItems.size();
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
    @SuppressWarnings("WeakerAccess")
    @Nullable
    protected Object getItemForBinding(final int position) {
        return adapterItems.get(position);
    }

    /**
     * Get the layout position of the item.
     * Headered list should override this.
     *
     * @param position the position of item in the item list.
     * @return the corresponding layout position in the recycler view.
     */
    @SuppressWarnings("WeakerAccess")
    protected int getItemLayoutPosition(final int position) {
        return position;
    }

    // endregion
}