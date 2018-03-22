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

package com.github.brianspace.databinding.message

import android.databinding.BindingAdapter
import android.view.View
import android.widget.Toast
import com.github.brianspace.databinding.message.IMessageSource.Type

/**
 * Databinding adapter for displaying messages with [IMessageSource].
 */

/**
 * Binding "messageSource" to display a message. Here Toast is used but more complex strategy may be applied.
 *
 * @param view the View which binds the message source.
 * @param messageSource the message source.
 */
@BindingAdapter("messageSource")
fun showMessage(view: View?, messageSource: IMessageSource?) {
    if (view == null || messageSource == null) {
        return
    }

    val duration = if (messageSource.type === Type.ERROR) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    Toast.makeText(view.context, messageSource.messageId, duration).show()
}
