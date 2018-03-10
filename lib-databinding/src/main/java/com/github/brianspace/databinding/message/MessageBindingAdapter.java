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

package com.github.brianspace.databinding.message;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.Toast;
import com.github.brianspace.databinding.message.IMessageSource.Type;

/**
 * Databinding adapter for displaying messages with {@link IMessageSource}.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class MessageBindingAdapter {
    // region Private Constants
    // endregion

    // region Constructors

    private MessageBindingAdapter() throws InstantiationException {
        throw new InstantiationException("Utility class MessageBindingAdapter should not be instantiated!");
    }

    // endregion

    // region Public Methods

    /**
     * Binding "messageSource" to display a message. Here Toast is used but more complex strategy may be applied.
     *
     * @param view the View which binds the message source.
     * @param oldMessageSource the last message source.
     * @param messageSource the message source.
     */
    @BindingAdapter({"messageSource"})
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public static void showMessage(final View view, final IMessageSource oldMessageSource,
            final IMessageSource messageSource) {
        if (view == null || messageSource == null || oldMessageSource == messageSource) {
            return;
        }

        final int duration = messageSource.getType() == Type.ERROR ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(view.getContext(), messageSource.getMessageId(), duration).show();
    }

    // endregion

    // region Private Methods
    // endregion
}