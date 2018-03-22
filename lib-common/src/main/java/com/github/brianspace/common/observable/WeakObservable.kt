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

import android.util.Log
import java.lang.ref.WeakReference
import java.security.InvalidParameterException
import java.util.ArrayList

/**
 * Use WeakReference for observers to prevent memory leak.
 * @see IObservable
 * @see IObserver
 *
 * @param <ObserverT> Type of the observers.
 */
abstract class WeakObservable<ObserverT> : IObservable<ObserverT> {

    // region Private Fields

    /**
     * The list of weak reference to the observers.
     */
    private var observers: MutableList<WeakReference<ObserverT>> = ArrayList()

    /**
     * Changed flag.
     */
    private var changed: Boolean = false

    // endregion

    // region Public Overrides

    override fun addObserver(observer: ObserverT) {
        if (observer == null) {
            throw InvalidParameterException("observer")
        }

        synchronized(this) {
            val found = observers.singleOrNull { it.get() === observer }
            if (found == null) {
                observers.add(WeakReference(observer))
            }
        }
    }

    override fun deleteObserver(observer: ObserverT) {
        synchronized(this) {
            val found = observers.singleOrNull { it.get() === observer }
            if (found != null) {
                observers.remove(found)
            }
        }
    }

    override fun deleteObservers() {
        synchronized(this) {
            observers.clear()
        }
    }


    override fun hasChanged(): Boolean {
        return changed
    }

    // endregion

    // region Protected Methods

    /**
     * Sets the changed flag for this `Observable`. After calling
     * `setChanged()`, `hasChanged()` will return `true`.
     */
    protected fun setChanged() {
        changed = true
    }

    /**
     * Clears the changed flag for this `Observable`. After calling
     * `clearChanged()`, `hasChanged()` will return `false`.
     */
    protected fun clearChanged() {
        changed = false
    }

    /**
     * Calls the specified action for each observer.
     * @param action action to be called.
     */
    protected fun foreachObserver(action: (ObserverT) -> Unit) {
        val tempObserverList = ArrayList<ObserverT>()
        val listToRemove = ArrayList<WeakReference<ObserverT>>()
        synchronized(this) {
            if (!hasChanged()) {
                return
            }

            clearChanged()

            if (observers.isEmpty()) {
                return
            }

            for (item in observers) {
                val observer: ObserverT? = item.get()
                if (observer == null) {
                    listToRemove.add(item)
                } else {
                    tempObserverList.add(observer)
                }
            }

            if (!listToRemove.isEmpty()) {
                observers.removeAll(listToRemove)
            }
        }

        notify(tempObserverList, action)
    }

    // endregion

    // region Private Methods

    private fun notify(tempObserverList: List<ObserverT>, action: (ObserverT) -> Unit) {
        tempObserverList.forEach {
            try {
                action(it)
            } catch (e: Exception) {
                Log.e("WeakObservable", e.toString())
            }
        }
    }

    // endregion
}
