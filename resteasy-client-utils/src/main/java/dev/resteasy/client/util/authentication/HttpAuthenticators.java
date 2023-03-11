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

package dev.resteasy.client.util.authentication;

import java.util.Objects;

import dev.resteasy.client.util.authentication.basic.BasicAuthorizationProcessor;
import dev.resteasy.client.util.authentication.digest.DigestAuthorizationProcessor;
import dev.resteasy.client.util.logging.ClientMessages;

/**
 * A utility to define the filter to place on a REST client or on a request.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class HttpAuthenticators {

    /**
     * Creates a {@linkplain jakarta.ws.rs.client.ClientRequestFilter client request filter} which will handle BASIC
     * authentication.
     *
     * @param credentials the authentication credentials
     *
     * @return the client request filter
     */
    public static HttpAuthorizationFilter basic(final UserCredentials credentials) {
        return new HttpAuthorizationFilter(new BasicAuthorizationProcessor(
                Objects.requireNonNull(credentials, ClientMessages.MESSAGES.requiredValue("credentials"))));
    }

    /**
     * Creates a {@linkplain jakarta.ws.rs.client.ClientRequestFilter client request filter} which will handle BASIC
     * authentication.
     *
     * @param limit       the maximum number of entries to cache for repeat requests
     * @param credentials the authentication credentials
     *
     * @return the client request filter
     */
    public static HttpAuthorizationFilter basic(final int limit, final UserCredentials credentials) {
        return new HttpAuthorizationFilter(limit, new BasicAuthorizationProcessor(
                Objects.requireNonNull(credentials, ClientMessages.MESSAGES.requiredValue("credentials"))));
    }

    /**
     * Creates a {@linkplain jakarta.ws.rs.client.ClientRequestFilter client request filter} which will handle DIGEST
     * authentication.
     *
     * @param credentials the authentication credentials
     *
     * @return the client request filter
     */
    public static HttpAuthorizationFilter digest(final UserCredentials credentials) {
        return new HttpAuthorizationFilter(new DigestAuthorizationProcessor(
                Objects.requireNonNull(credentials, ClientMessages.MESSAGES.requiredValue("credentials"))));
    }

    /**
     * Creates a {@linkplain jakarta.ws.rs.client.ClientRequestFilter client request filter} which will handle DIGEST
     * authentication.
     *
     * @param limit       the maximum number of entries to cache for repeat requests
     * @param credentials the authentication credentials
     *
     * @return the client request filter
     */
    public static HttpAuthorizationFilter digest(final int limit, final UserCredentials credentials) {
        return new HttpAuthorizationFilter(limit, new DigestAuthorizationProcessor(
                Objects.requireNonNull(credentials, ClientMessages.MESSAGES.requiredValue("credentials")), limit));
    }

    /**
     * Creates a {@linkplain jakarta.ws.rs.client.ClientRequestFilter client request filter} for BASIC and DIGEST
     * authentication.
     *
     * @param credentials the authentication credentials
     *
     * @return the client request filter
     */
    public static HttpAuthorizationFilter available(final UserCredentials credentials) {
        Objects.requireNonNull(credentials, ClientMessages.MESSAGES.requiredValue("credentials"));
        return new HttpAuthorizationFilter(new BasicAuthorizationProcessor(credentials),
                new DigestAuthorizationProcessor(credentials));
    }

    /**
     * Creates a {@linkplain jakarta.ws.rs.client.ClientRequestFilter client request filter} for BASIC and DIGEST
     * authentication.
     *
     * @param limit       the maximum number of entries to cache for repeat requests
     * @param credentials the authentication credentials
     *
     * @return the client request filter
     */
    public static HttpAuthorizationFilter available(final int limit, final UserCredentials credentials) {
        Objects.requireNonNull(credentials, ClientMessages.MESSAGES.requiredValue("credentials"));
        return new HttpAuthorizationFilter(limit, new BasicAuthorizationProcessor(credentials),
                new DigestAuthorizationProcessor(credentials));
    }
}
