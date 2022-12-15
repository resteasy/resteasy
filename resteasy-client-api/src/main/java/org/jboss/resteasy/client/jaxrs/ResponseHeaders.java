/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.client.jaxrs;

import java.util.Locale;
import java.util.Map;

import jakarta.ws.rs.core.HttpHeaders;

/**
 * In HTTP/1.1 header names are supposed to case-insensitive. However, there is a pseudo standard of using names like
 * {@code Content-Type}. Some clients may create a response with headers like {@code content-type} as in HTTP/2 there
 * is a requirement for lowercase names. This simple utility just helps translate some common header names.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResponseHeaders {

    private static final Map<String, String> COMMON = Map.ofEntries(
            // Jakarta REST standards
            createEntry(HttpHeaders.ACCEPT),
            createEntry(HttpHeaders.ACCEPT_CHARSET),
            createEntry(HttpHeaders.ACCEPT_ENCODING),
            createEntry(HttpHeaders.ACCEPT_LANGUAGE),
            createEntry(HttpHeaders.ALLOW),
            createEntry(HttpHeaders.AUTHORIZATION),
            createEntry(HttpHeaders.CACHE_CONTROL),
            createEntry(HttpHeaders.CONTENT_DISPOSITION),
            createEntry(HttpHeaders.CONTENT_ENCODING),
            createEntry(HttpHeaders.CONTENT_ID),
            createEntry(HttpHeaders.CONTENT_LANGUAGE),
            createEntry(HttpHeaders.CONTENT_LENGTH),
            createEntry(HttpHeaders.CONTENT_LOCATION),
            createEntry(HttpHeaders.CONTENT_TYPE),
            createEntry(HttpHeaders.COOKIE),
            createEntry(HttpHeaders.DATE),
            createEntry(HttpHeaders.ETAG),
            createEntry(HttpHeaders.EXPECT),
            createEntry(HttpHeaders.EXPIRES),
            createEntry(HttpHeaders.HOST),
            createEntry(HttpHeaders.IF_MATCH),
            createEntry(HttpHeaders.IF_MODIFIED_SINCE),
            createEntry(HttpHeaders.IF_NONE_MATCH),
            createEntry(HttpHeaders.IF_UNMODIFIED_SINCE),
            createEntry(HttpHeaders.LAST_EVENT_ID_HEADER),
            createEntry(HttpHeaders.LAST_MODIFIED),
            createEntry(HttpHeaders.LINK),
            createEntry(HttpHeaders.LOCATION),
            createEntry(HttpHeaders.RETRY_AFTER),
            createEntry(HttpHeaders.SET_COOKIE),
            createEntry(HttpHeaders.USER_AGENT),
            createEntry(HttpHeaders.VARY),
            createEntry(HttpHeaders.WWW_AUTHENTICATE));

    /**
     * Translates a lowercase header name to it's mixed case equivalent. If there is no known alternative the header
     * name itself is returned.
     * <p>
     * An example would be passing {@code user-agent} would return {@code User-Agent}.
     * </p>
     *
     * @param headerName the header name to attempt to translate, cannot be {@code null}
     *
     * @return the known translated header name or the header name itself if it is unknown
     */
    public static String lowerToDefault(final String headerName) {
        return COMMON.getOrDefault(headerName, headerName);
    }

    /**
     * This is a helper to covert a header name to lower case. This simply does {@code headerName.toLowerCase(Locale.ROOT}.
     *
     * @param headerName the header name to convert
     *
     * @return the header name in lowercase
     */
    public static String toLower(final String headerName) {
        return headerName.toLowerCase(Locale.ROOT);
    }

    private static Map.Entry<String, String> createEntry(final String headerName) {
        return Map.entry(headerName.toLowerCase(Locale.ROOT), headerName);
    }
}
