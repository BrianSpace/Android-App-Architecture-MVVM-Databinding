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

package com.github.brianspace.common.observable;

import android.support.annotation.Nullable;
import java.util.List;

/**
 * Base class for the collection which is observable for changes in the list.
 */
public class CollectionObservableBase extends WeakObservable<ICollectionObserver> {

    /**
     * Notify observers for the change in the list.
     *
     * @param action    {@code ICollectionObserver.Action} on the list.
     * @param item      the item that changed.
     * @param range     the range of items that changed.
     */
    @SuppressWarnings("unchecked")
    protected void notifyObservers(final ICollectionObserver.Action action, @Nullable final Object item,
            @Nullable final List<Object> range) {
        foreachObserver(observer -> observer.onUpdate(this, action, item, range));
    }
}
