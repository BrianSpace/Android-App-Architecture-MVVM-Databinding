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

import android.support.annotation.StringRes;

/**
 * Implementation class for displaying messages with {@link IMessageSource}.
 */
public class MessageSource implements IMessageSource {

    // region Private Fields

    /**
     * Resource ID of the message string to be displayed.
     */
    private final int messageId;

    /**
     * Type of the message.
     */
    private final Type messageType;

    // endregion

    // region Constructors

    /**
     * Create a new instance of the MessageSource.
     *
     * @param messageId resource ID of the message string to be displayed.
     * @param type      message type.
     */
    public MessageSource(@StringRes final int messageId, final Type type) {
        this.messageId = messageId;
        this.messageType = type;
    }

    // endregion

    // region Public Overrides

    @Override
    public int getMessageId() {
        return messageId;
    }

    @Override
    public Type getType() {
        return messageType;
    }

    // endregion
}