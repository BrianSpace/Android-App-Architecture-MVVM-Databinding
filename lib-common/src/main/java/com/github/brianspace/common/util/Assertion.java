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

package com.github.brianspace.common.util;

/**
 * Utility class for assertions.
 */
public final class Assertion {

    // region Constructors

    private Assertion() throws InstantiationException {
        throw new InstantiationException("Utility class Assertion should not be instantiated!");
    }

    // endregion

    // region Public Methods

    /**
     * Assert if a value is null, and returns the value if it is not null.
     * Use java.util.Objects.requireNonNull() instead if your min API level is 19,
     * or Guava's com.google.common.base.Preconditions.checkNotNull() method if you like.
     *
     * @param target the object to be checked.
     * @param <T> the type of the object to be checked.
     * @return the object itself if it is not null.
     */
    public static <T> T notNull(final T target) {
        if (target == null) {
            throw new AssertionError("Object should not be null.");
        }

        return target;
    }

    // endregion
}
