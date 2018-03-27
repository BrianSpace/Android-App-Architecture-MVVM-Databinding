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
 * Base class for a single observable object.
 */
open class ObjectObservableBase : WeakObservable<IObserver>() {

    /**
     * If `hasChanged()` returns `true`, calls the `onUpdate()`
     * method for every observer in the list of observers using null as the
     * argument. Afterwards, calls `clearChanged()`.
     *
     *
     * Equivalent to calling `notifyObservers(null)`.
     */
    protected fun notifyObservers() {
        notifyObservers(null)
    }

    /**
     * If `hasChanged()` returns `true`, calls the `onUpdate()`
     * method for every Observer in the list of observers using the specified
     * argument. Afterwards calls `clearChanged()`.
     *
     * @param data the argument passed to `onUpdate()`.
     */
    protected fun notifyObservers(data: Any?) {
        foreachObserver { it.onUpdate(this, data) }
    }
}
