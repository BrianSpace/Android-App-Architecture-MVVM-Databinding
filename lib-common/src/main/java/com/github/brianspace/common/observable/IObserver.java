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
import android.support.annotation.Nullable;

/**
 * Interface for observers.
 */
public interface IObserver {
    /**
     * When {@code IObservable} object changes, {@code WeakObservable#notifyObservers} will be called and this method of
     * each observer will be called.
     *
     * @param observable    {@link IObservable} instance.
     * @param data          Object passed to {@link ObjectObservableBase#notifyObservers(Object)} when a specific item
     *                      in a collection changed.
     */
    void onUpdate(@NonNull IObservable<IObserver> observable, @Nullable Object data);
}
