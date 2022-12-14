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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

import dev.resteasy.client.util.common.LimitMap;

/**
 * A {@link ClientResponseFilter} which repeats the request if the response from the original request has a status of
 * {@link Response.Status#UNAUTHORIZED}. It uses the {@link AuthorizationProcessor}'s provided to add a
 * {@link HttpHeaders#AUTHORIZATION} header created from the processor.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ConstrainedTo(RuntimeType.CLIENT)
public class HttpAuthorizationFilter implements ClientRequestFilter, ClientResponseFilter {
    private static final Logger LOGGER = Logger.getLogger(HttpAuthorizationFilter.class);
    private static final String REQUEST_PROCESSED = "org.jboss.resteasy.client.authentication.request.processed";
    private static final String RESPONSE_PROCESSED = "org.jboss.resteasy.client.authentication.response.processed";
    private final Collection<AuthorizationProcessor> processors;
    private final Map<String, AuthorizationProcessor> cache;

    /**
     * Creates a new filter with a default limit for the cached size.
     *
     * @param processors the processors used to create the authorization header
     */
    public HttpAuthorizationFilter(final AuthorizationProcessor... processors) {
        this(100, Arrays.asList(processors));
    }

    /**
     * Creates a new filter with a default limit for the cached size.
     *
     * @param limit      the maximum number of entries to cache for repeat requests
     * @param processors the processors used to create the authorization header
     */
    public HttpAuthorizationFilter(final int limit, final AuthorizationProcessor... processors) {
        this(limit, Arrays.asList(processors));
    }

    /**
     * Creates a new filter.
     *
     * @param limit      the maximum number of entries to cache for repeat requests
     * @param processors the processors used to create the authorization header
     */
    public HttpAuthorizationFilter(final int limit, final Collection<AuthorizationProcessor> processors) {
        this.processors = new ArrayList<>(processors);
        cache = limit > 0 ? LimitMap.of(limit) : null;
    }

    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {
        if (requestContext.hasProperty(REQUEST_PROCESSED) || requestContext.hasProperty(RESPONSE_PROCESSED)) {
            return;
        }
        if (requestContext.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return;
        }
        requestContext.setProperty(REQUEST_PROCESSED, true);
        if (cache == null) {
            return;
        }
        final String key = createKey(requestContext);
        final AuthorizationProcessor cached = cache.get(key);
        if (cached != null) {
            final String value = cached.createRequestHeader(requestContext);
            if (value == null) {
                cache.remove(key);
                cached.reset(requestContext);
            } else {
                requestContext.setProperty(RESPONSE_PROCESSED, true);
                requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, value);
            }
        }
    }

    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext)
            throws IOException {

        if (requestContext.getProperty(RESPONSE_PROCESSED) != null) {
            return;
        }

        if (responseContext.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
            final List<String> authenticateHeader = responseContext.getHeaders().get(HttpHeaders.WWW_AUTHENTICATE);
            for (AuthorizationProcessor processor : processors) {
                if (repeatRequest(requestContext, responseContext,
                        processor.createRequestHeader(requestContext, authenticateHeader))) {
                    if (cache != null) {
                        cache.put(createKey(requestContext), processor);
                    }
                    break;
                } else {
                    processor.reset(requestContext);
                    LOGGER.debugf("Failed to process request for type %s with processor %s", authenticateHeader,
                            processor.getClass().getName());
                }
            }
        }
    }

    private static boolean repeatRequest(final ClientRequestContext request, final ClientResponseContext response,
            final String authHeader) {
        if (authHeader == null) {
            return false;
        }
        final Client client = request.getClient();
        final String method = request.getMethod();
        final MediaType mediaType = request.getMediaType();

        final Invocation.Builder builder = client.target(request.getUri()).request(mediaType);
        final MultivaluedMap<String, Object> newHeaders = new MultivaluedHashMap<>();

        for (Map.Entry<String, List<Object>> entry : request.getHeaders().entrySet()) {
            if (HttpHeaders.AUTHORIZATION.equals(entry.getKey())) {
                continue;
            }
            newHeaders.put(entry.getKey(), entry.getValue());
        }

        newHeaders.add(HttpHeaders.AUTHORIZATION, authHeader);
        builder.headers(newHeaders);
        builder.property(RESPONSE_PROCESSED, true);

        final Invocation invocation;
        if (request.getEntity() == null) {
            invocation = builder.build(method);
        } else {
            invocation = builder.build(method,
                    Entity.entity(request.getEntity(), request.getMediaType()));
        }
        final Response newResponse = invocation.invoke();

        if (newResponse.hasEntity()) {
            response.setEntityStream(newResponse.readEntity(InputStream.class));
        }
        final MultivaluedMap<String, String> headers = response.getHeaders();
        headers.clear();
        headers.putAll(newResponse.getStringHeaders());
        response.setStatus(newResponse.getStatus());
        return response.getStatus() != Response.Status.UNAUTHORIZED.getStatusCode();
    }

    private static String createKey(final ClientRequestContext request) {
        final URI requestUri = request.getUri();
        if (requestUri.getRawQuery() != null) {
            // Remove the query from the URI
            try {
                return formatKey(new URI(
                        requestUri.getScheme(),
                        requestUri.getAuthority(),
                        requestUri.getPath(),
                        null,
                        requestUri.getFragment()), request.getMethod());
            } catch (URISyntaxException ignore) {
            }
        }
        return formatKey(requestUri, request.getMethod());
    }

    private static String formatKey(final URI requestUri, final String method) {
        return String.format("%s:%s", requestUri, method);
    }
}
