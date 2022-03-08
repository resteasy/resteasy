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

import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;

/**
 * A processor for creating a value for the {@link jakarta.ws.rs.core.HttpHeaders#AUTHORIZATION} header. The
 * {@link #createRequestHeader(ClientRequestContext)} is first invoked to see if we can create a header without first making
 * the request. If not, e.g. {@code null} is returned, then the {@link #createRequestHeader(ClientRequestContext, List)}
 * is invoked.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@FunctionalInterface
public interface AuthorizationProcessor {

    /**
     * Creates a value for the {@link jakarta.ws.rs.core.HttpHeaders#AUTHORIZATION} header.
     *
     * @param requestContext     the client request context
     * @param authenticateHeader the {@link jakarta.ws.rs.core.HttpHeaders#WWW_AUTHENTICATE} value
     *
     * @return the value for the {@link jakarta.ws.rs.core.HttpHeaders#AUTHORIZATION} header or {@link null} if one
     * could not be created by this processor
     */
    String createRequestHeader(ClientRequestContext requestContext, List<String> authenticateHeader);

    /**
     * Creates a value for the {@link jakarta.ws.rs.core.HttpHeaders#AUTHORIZATION} header.
     *
     * @param requestContext the client request context
     *
     * @return the value for the {@link jakarta.ws.rs.core.HttpHeaders#AUTHORIZATION} header or {@link null} if one
     * could not be created by this processor
     */
    default String createRequestHeader(ClientRequestContext requestContext) {
        return null;
    }

    /**
     * Processors may require a reset if authorization fails. By default, this does nothing.
     *
     * @param requestContext the client request context
     */
    default void reset(ClientRequestContext requestContext) {
        // do nothing by default
    }
}
