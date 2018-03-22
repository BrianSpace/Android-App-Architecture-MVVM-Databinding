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

package com.github.brianspace.common.observable

/**
 * Interface for observers on a collection.
 */
interface ICollectionObserver {

    /**
     * The action on the collection.
     */
    enum class Action {
        Clear,          // Collection cleared.
        AppendItem,     // Append an item to the end.
        AppendRange,    // Append a list to the end.
        AddItemToFront, // Add item to the front.
        RemoveItem,     // Remove item.
        UpdateItem      // Update item.
    }

    /**
     * When `IObservable` object changes, `WeakObservable#notifyObservers` will be called and this method of
     * each observer will be called.
     *
     * @param observable    [IObservable] instance.
     * @param action        [Action] happened.
     * @param item          Item in the collection which was modified
     *                      (AppendItem, AddItemToFront, RemoveItem, UpdateItem).
     * @param range         The range changed (AppendRange).
     */
    fun onUpdate(observable: IObservable<ICollectionObserver>, action: Action, item: Any?, range: List<Any>?)
}
