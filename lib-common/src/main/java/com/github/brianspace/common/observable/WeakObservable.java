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

import android.support.annotation.NonNull;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Use WeakReference for observers to prevent memory leak.
 * @see IObservable
 * @see IObserver
 *
 * @param <ObserverT> Type of the observers.
 */
public abstract class WeakObservable<ObserverT> implements IObservable<ObserverT> {

    // region Private Fields

    /**
     * The list of weak reference to the observers.
     */
    protected List<WeakReference<ObserverT>> observers = new ArrayList<>();

    /**
     * Changed flag.
     */
    private boolean changed;

    // endregion

    // region Public Overrides

    @Override
    public void addObserver(@NonNull final ObserverT observer) {
        if (observer == null) {
            throw new InvalidParameterException("observer");
        }

        synchronized (this) {
            boolean found = false;
            for (final WeakReference<ObserverT> item : observers) {
                if (item.get() == observer) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                observers.add(new WeakReference<>(observer));
            }
        }
    }

    @Override
    public void deleteObserver(@NonNull final ObserverT observer) {
        synchronized (this) {
            WeakReference<ObserverT> found = null;
            for (final WeakReference<ObserverT> item : observers) {
                if (item.get() == observer) {
                    found = item;
                }
            }

            if (found != null) {
                observers.remove(found);
            }
        }
    }

    @Override
    public void deleteObservers() {
        synchronized (this) {
            observers.clear();
        }
    }

    @Override
    public boolean hasChanged() {
        return changed;
    }

    // endregion

    // region Protected Methods

    /**
     * Sets the changed flag for this {@code Observable}. After calling
     * {@code setChanged()}, {@code hasChanged()} will return {@code true}.
     */
    protected void setChanged() {
        changed = true;
    }

    /**
     * Clears the changed flag for this {@code Observable}. After calling
     * {@code clearChanged()}, {@code hasChanged()} will return {@code false}.
     */
    protected void clearChanged() {
        changed = false;
    }

    /**
     * Interface for the call back of notification.
     */
    protected interface INotificationCallback<T> {

        /**
         * Method to be called when a notification comes.
         * @param observer the observer object.
         */
        void onNotify(T observer);
    }

    /**
     * Calls the specified action for each observer.
     * @param action action to be called.
     */
    protected void foreachObserver(@NonNull final INotificationCallback<ObserverT> action) {
        final List<ObserverT> tempObserverList = new ArrayList<>();
        final List<WeakReference<ObserverT>> listToRemove = new ArrayList<>();
        synchronized (this) {
            if (!hasChanged()) {
                return;
            }

            clearChanged();

            if (observers.isEmpty()) {
                return;
            }

            for (final WeakReference<ObserverT> item : observers) {
                if (item.get() == null) {
                    listToRemove.add(item);
                } else {
                    tempObserverList.add(item.get());
                }
            }

            if (!listToRemove.isEmpty()) {
                observers.removeAll(listToRemove);
            }
        }

        notify(tempObserverList, action);
    }

    // endregion

    // region Private Methods

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void notify(final List<ObserverT> tempObserverList, final INotificationCallback<ObserverT> action) {
        if (!tempObserverList.isEmpty()) {
            for (final ObserverT observer : tempObserverList) {
                try {
                    action.onNotify(observer);
                } catch (final Exception e) {
                    Log.e("WeakObservable", e.getMessage());
                }
            }
        }
    }

    // endregion
}
