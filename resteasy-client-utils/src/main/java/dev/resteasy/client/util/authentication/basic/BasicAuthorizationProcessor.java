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

package dev.resteasy.client.util.authentication.basic;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

import jakarta.ws.rs.client.ClientRequestContext;

import dev.resteasy.client.util.authentication.AuthorizationProcessor;
import dev.resteasy.client.util.authentication.UserCredentials;
import dev.resteasy.client.util.common.ByteStringBuilder;
import dev.resteasy.client.util.logging.ClientMessages;

/**
 * An {@link AuthorizationProcessor} which creates a {@link jakarta.ws.rs.core.HttpHeaders#AUTHORIZATION} header for
 * BASIC authentication.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class BasicAuthorizationProcessor implements AuthorizationProcessor {
    private static final String CHALLENGE_PREFIX = "Basic ";
    private final UserCredentials credentials;

    /**
     * Creates a new BASIC processor.
     *
     * @param credentials the credentials to use for authentication
     */
    public BasicAuthorizationProcessor(final UserCredentials credentials) {
        this.credentials = Objects.requireNonNull(credentials, ClientMessages.MESSAGES.requiredValue("credentials"));
    }

    @Override
    public String createRequestHeader(final ClientRequestContext requestContext) {
        final ByteStringBuilder builder = new ByteStringBuilder(128)
                .append(credentials.getUsername())
                .append(':')
                .append(credentials.getPassword());
        return CHALLENGE_PREFIX + Base64.getEncoder().encodeToString(builder.toArray());
    }

    @Override
    public String createRequestHeader(final ClientRequestContext requestContext,
            final List<String> authenticateHeader) {
        boolean process = false;
        for (String challenge : authenticateHeader) {
            if (challenge.regionMatches(true, 0, CHALLENGE_PREFIX, 0, CHALLENGE_PREFIX.length())) {
                process = true;
                break;
            }
        }
        return process ? createRequestHeader(requestContext) : null;
    }
}
