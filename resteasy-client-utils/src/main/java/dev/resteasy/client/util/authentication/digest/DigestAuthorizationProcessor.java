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

package dev.resteasy.client.util.authentication.digest;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.ws.rs.client.ClientRequestContext;

import dev.resteasy.client.util.authentication.AuthorizationProcessor;
import dev.resteasy.client.util.authentication.UserCredentials;
import dev.resteasy.client.util.common.Bytes;
import dev.resteasy.client.util.common.LimitMap;
import dev.resteasy.client.util.logging.ClientMessages;

/**
 * An {@link AuthorizationProcessor} which creates a {@link jakarta.ws.rs.core.HttpHeaders#AUTHORIZATION} header for
 * DIGEST authentication.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class DigestAuthorizationProcessor implements AuthorizationProcessor {
    private static final String CHALLENGE_PREFIX = "Digest ";

    private final UserCredentials credentials;
    private final Map<URI, DigestScheme> cache;
    private final SecureRandom generator;

    /**
     * Creates a new DIGEST processor.
     *
     * @param credentials the credentials to use for authentication
     */
    public DigestAuthorizationProcessor(final UserCredentials credentials) {
        this(credentials, 100);
    }

    /**
     * Creates a new DIGEST processor.
     *
     * @param credentials the credentials to use for authentication
     * @param limit       the maximum number of entries to cache for repeat requests
     */
    public DigestAuthorizationProcessor(final UserCredentials credentials, final int limit) {
        this.credentials = Objects.requireNonNull(credentials, ClientMessages.MESSAGES.requiredValue("credentials"));
        cache = limit > 0 ? LimitMap.of(limit) : null;
        generator = new SecureRandom();
    }

    @Override
    public String createRequestHeader(final ClientRequestContext requestContext) {
        if (cache != null) {
            final DigestScheme digest = cache.get(requestContext.getUri());
            if (digest != null) {
                return createHeaderValue(digest, requestContext);
            }
        }
        return null;
    }

    @Override
    public String createRequestHeader(final ClientRequestContext requestContext,
            final List<String> authenticateHeader) {
        for (String authHeader : authenticateHeader) {
            if (!authHeader.regionMatches(true, 0, CHALLENGE_PREFIX, 0, CHALLENGE_PREFIX.length())) {
                continue;
            }
            final DigestScheme digestScheme = DigestScheme.of(authHeader);
            if (digestScheme != null) {
                return createHeaderValue(digestScheme, requestContext);
            }
        }
        return null;
    }

    @Override
    public void reset(final ClientRequestContext requestContext) {
        if (cache != null) {
            cache.remove(requestContext.getUri());
        }
    }

    private String createHeaderValue(final DigestScheme digest, final ClientRequestContext requestContext) {
        if (digest.algorithm == null) {
            return null;
        }
        final StringBuilder result = new StringBuilder(100);
        result.append(CHALLENGE_PREFIX);
        final String username = credentials.getUsername();
        if (digest.userhash) {
            append(result, "username", digest.algorithm.builder().append(username).build());
            append(result, "userhash", true, false);
        } else {
            boolean encode = false;
            for (int c : username.toCharArray()) {
                if (c < 0 || c >= 127) {
                    encode = true;
                    break;
                }
            }
            if (encode) {
                append(result, "username*", "UTF-8''" + URLEncoder.encode(username, StandardCharsets.UTF_8), false);
            } else {
                append(result, "username", username);
            }
        }
        append(result, "realm", digest.realm);
        append(result, "qop", digest.qop, false);
        append(result, "nonce", digest.nonce);
        append(result, "opaque", digest.opaque);
        append(result, "algorithm", digest.algorithm, false);

        final String uri = relativeUri(requestContext.getUri());
        append(result, "uri", uri);
        final byte[] bytes = new byte[32];
        generator.nextBytes(bytes);
        final String cnonce = Bytes.bytesToHexString(bytes);

        final String ha1;
        if (digest.algorithm.isSession()) {
            ha1 = digest.algorithm.builder()
                    .append(username)
                    .append(digest.realm)
                    .append(credentials.getPassword())
                    .append(digest.nonce)
                    .append(cnonce)
                    .build();
        } else {
            ha1 = digest.algorithm.builder()
                    .append(username)
                    .append(digest.realm)
                    .append(credentials.getPassword())
                    .build();
        }

        final DigestHashBuilder a2Builder = digest.algorithm.builder()
                .append(requestContext.getMethod())
                .append(uri);
        if ("auth-init".equalsIgnoreCase(digest.qop)) {
            final Object entity = requestContext.getEntity();
            if (entity != null) {
                a2Builder.append(digest.algorithm.builder().append(entity).build());
            }
        }
        final String ha2 = a2Builder.build();

        final String response;
        if (digest.qop == null) {
            response = digest.algorithm.builder()
                    .append(ha1)
                    .append(digest.nonce)
                    .append(ha2)
                    .build();
        } else {
            append(result, "cnonce", cnonce);
            final String nc = String.format("%08x", digest.nc.incrementAndGet());
            append(result, "nc", nc, false);
            response = digest.algorithm.builder()
                    .append(ha1)
                    .append(digest.nonce)
                    .append(nc)
                    .append(cnonce)
                    .append(digest.qop)
                    .append(ha2)
                    .build();
        }
        append(result, "response", response);

        if (cache != null) {
            cache.put(requestContext.getUri(), digest);
        }
        return result.toString();
    }

    private static void append(final StringBuilder sb, final String key, final String value) {
        append(sb, key, value, true);
    }

    private static void append(final StringBuilder sb, final String key, final Object value, final boolean quoted) {
        if (value != null) {
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
                sb.append(',');
            }
            sb.append(key)
                    .append('=');
            if (quoted) {
                sb.append('"');
            }
            sb.append(value);
            if (quoted) {
                sb.append('"');
            }
        }
    }

    private static String relativeUri(final URI uri) {
        if (uri == null) {
            return null;
        }
        final String query = uri.getRawQuery();
        return uri.getRawPath() + (query != null && query.length() > 0 ? "?" + query : "");
    }
}
