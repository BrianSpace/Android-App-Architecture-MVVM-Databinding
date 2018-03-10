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

package com.github.brianspace.common.objstore;

import android.support.annotation.NonNull;

import com.github.brianspace.common.util.Function;
import java.lang.ref.WeakReference;

/**
 * Object store for two layer of objects, which ensures that only one object will be associated with one key.
 * @param <M>  Type of the upper layer objects, which are created based on the lower layer objects.
 * @param <D>  Type of the lower layer objects.
 */
public final class ModelObjectStore<M, D extends IEntity> extends ObjectStore<M> {

    // region Private Fields

    /**
     * Function to create an upper layer object of type M from lower layer object of type D.
     */
    private final Function<D, M> modelCreator;

    // endregion

    // region Constructors

    /**
     * Create a new instance of the ModelObjectStore.
     * @param modelCreator function to create an upper layer object from lower layer object.
     */
    public ModelObjectStore(@NonNull final Function<D, M> modelCreator) {
        this.modelCreator = modelCreator;
    }

    // endregion

    // region Public Methods

    /**
     * Get model object from the data layer object, or create a new one if not found.
     * @param lowerObj the lower layer object.
     * @return the model matching the lower layer object.
     */
    @NonNull
    public M getOrCreate(final D lowerObj) {
        rwLock.readLock().lock(); // Read lock
        try {
            final int key = lowerObj.getId();
            final WeakReference<M> found = cache.get(key);
            if (found != null) {
                final M item = found.get();
                if (item != null) {
                    return item;
                }
            }

            // Upgrade to write lock
            rwLock.readLock().unlock();
            rwLock.writeLock().lock();
            try {
                final M model = modelCreator.apply(lowerObj);
                cache.put(lowerObj.getId(), new WeakReference<>(model));
                return model;
            } finally {
                // Downgrade to read lock
                rwLock.readLock().lock();
                rwLock.writeLock().unlock();
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    // endregion
}
