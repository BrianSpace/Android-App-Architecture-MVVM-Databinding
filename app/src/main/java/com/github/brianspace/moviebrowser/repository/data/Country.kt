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

package com.github.brianspace.moviebrowser.repository.data

import com.google.gson.annotations.SerializedName

/**
 * Country.
 */
class Country {
    /**
     * The short country name in ISO 3166-1 format.
     * @see [ISO 3166-1](https://en.wikipedia.org/wiki/ISO_3166-1)
     */
    @SerializedName("iso_3166_1")
    val shortName: String? = null

    /**
     * The country name.
     */
    val name: String? = null
}
