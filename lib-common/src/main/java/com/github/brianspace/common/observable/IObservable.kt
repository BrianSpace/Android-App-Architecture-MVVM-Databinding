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
 * Interface for observable objects.
 *
 * @param <ObserverT> Type of the observers.
 */
interface IObservable<ObserverT> {

    /**
     * Adds the specified observer to the list of observers. If it is already registered, it is not added a second time.
     * Note: make sure this is not the only reference to the observer, otherwise it may be garbage collected.
     *
     * @param observer the Observer to add.
     */
    fun addObserver(observer: ObserverT)

    /**
     * Removes the specified observer from the list of observers. Passing null won't do anything.
     *
     * @param observer the observer to remove.
     */
    fun deleteObserver(observer: ObserverT)

    /**
     * Removes all observers from the list of observers.
     */
    fun deleteObservers()

    /**
     * Returns the changed flag for this `Observable`.
     *
     * @return `true` when the changed flag for this `Observable` is set, `false` otherwise.
     */
    fun hasChanged(): Boolean
}
