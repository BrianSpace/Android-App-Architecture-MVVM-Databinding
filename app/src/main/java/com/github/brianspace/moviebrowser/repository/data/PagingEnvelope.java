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

package com.github.brianspace.moviebrowser.repository.data;

import java.util.List;

/**
 * Envelope for paged list result.
 * @param <T> Type of the item in the paged list.
 */
@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class PagingEnvelope<T> {
    private final int totalPages;
    private final int totalResults;
    private final int page;
    private final List<T> results;

    /**
     * Get the total number of pages.
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Get the total number of result items.
     */
    public int getTotalResults() {
        return totalResults;
    }

    /**
     * Get the page number in the current page.
     */
    public int getPage() {
        return page;
    }

    /**
     * Get the list of items in the current page.
     */
    public List<T> getResults() {
        return results;
    }

    /**
     * Constructor.
     * @param totalPages total number of pages.
     * @param totalResults total number of results.
     * @param page the current page number.
     * @param results result list.
     */
    public PagingEnvelope(final int totalPages, final int totalResults, final int page, final List<T> results) {
        this.totalPages = totalPages;
        this.totalResults = totalResults;
        this.page = page;
        this.results = results;
    }
}
