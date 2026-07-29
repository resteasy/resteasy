/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.request;

/**
 * How precisely a variant's language matched an entry in the {@code Accept-Language} header.
 * <p>
 * Ordinal order reflects match quality: {@link #WILDCARD} (worst) through {@link #EXACT_COUNTRY} (best).
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public enum LanguageMatchPrecision {
    /**
     * Matched only via the wildcard ({@code *}) entry.
     */
    WILDCARD,
    /**
     * Base language matched but country presence differs (e.g. variant {@code en} matched request {@code en-US}).
     */
    BASE_LANGUAGE,
    /**
     * Exact match with no country on either side (e.g. variant {@code de} matched request {@code de}).
     */
    EXACT,
    /**
     * Exact match including country (e.g. variant {@code en-GB} matched request {@code en-GB}).
     */
    EXACT_COUNTRY
}
