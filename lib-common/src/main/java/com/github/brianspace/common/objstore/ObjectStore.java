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

import android.support.annotation.Nullable;
import android.util.SparseArray;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Object store, which ensures that only one object will be associated with one key.
 * @param <T>  Type of the entity class.
 */
public class ObjectStore<T> {
    // region Protected Fields

    /**
     * Array of weak reference to the cached objects.
     */
    @SuppressWarnings("WeakerAccess")
    protected final SparseArray<WeakReference<T>> cache = new SparseArray<>();

    /**
     * Lock for concurrent read/write.
     * Can be replaced by StampedLock in the future.
     */
    @SuppressWarnings("WeakerAccess")
    protected final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    // endregion

    // region Public Methods

    /**
     * Find model object by key.
     * @param key the key
     * @return the model matching the key, or null if not found.
     */
    @Nullable
    public T find(final int key) {
        rwLock.readLock().lock(); // Read lock
        try {
            final WeakReference<T> found = cache.get(key);
            if (found != null) {
                final T item = found.get();
                if (item != null) {
                    return item;
                }

                // Reference need to be cleared.
                // Upgrade to write lock
                rwLock.readLock().unlock();
                rwLock.writeLock().lock();
                try {
                    cache.delete(key);
                } finally {
                    // Downgrade to read lock
                    rwLock.readLock().lock();
                    rwLock.writeLock().unlock();
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }

        return null;
    }

    // endregion
}