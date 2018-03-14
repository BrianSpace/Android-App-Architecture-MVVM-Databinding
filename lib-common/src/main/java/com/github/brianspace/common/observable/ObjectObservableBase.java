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

/**
 * Base class for a single observable object.
 */
public class ObjectObservableBase extends WeakObservable<IObserver> {

    /**
     * If {@code hasChanged()} returns {@code true}, calls the {@code onUpdate()}
     * method for every observer in the list of observers using null as the
     * argument. Afterwards, calls {@code clearChanged()}.
     *
     * <p>Equivalent to calling {@code notifyObservers(null)}.
     */
    protected void notifyObservers() {
        notifyObservers(null);
    }

    /**
     * If {@code hasChanged()} returns {@code true}, calls the {@code onUpdate()}
     * method for every Observer in the list of observers using the specified
     * argument. Afterwards calls {@code clearChanged()}.
     *
     * @param data the argument passed to {@code onUpdate()}.
     */
    @SuppressWarnings("unchecked")
    protected void notifyObservers(@Nullable final Object data) {
        foreachObserver(observer -> observer.onUpdate(this, data));
    }
}
