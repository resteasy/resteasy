/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.servlet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.function.Function;

import jakarta.servlet.http.HttpServletResponse;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StreamValueInjector;

/**
 * Injects a {@linkplain HttpServletResponse} proxy which delegates to a {@link ResteasyHttpServletResponse} stored on
 * the current request. This allows the content to be checked if it was written to, while still being safe for targets,
 * such as singletons, that outlive a single request.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class HttpServletResponseValueInjector implements StreamValueInjector {
    private static final String SERVLET_RESPONSE_ATTRIBUTE = ResteasyHttpServletResponse.class.getName();

    private final Function<HttpRequest, HttpServletResponse> delegateResolver;
    private final ResteasyProviderFactory factory;

    /**
     * Creates a new injector factory.
     *
     * @param factory the factory used to look up the context data
     */
    public HttpServletResponseValueInjector(final ResteasyProviderFactory factory) {
        this(factory, request -> factory.getContextData(HttpServletResponse.class));
    }

    /**
     * Creates a new injector factory.
     *
     * @param factory          the factory used to look up the context data
     * @param delegateResolver a resolver which can look up the {@link HttpServletResponse}
     */
    public HttpServletResponseValueInjector(final ResteasyProviderFactory factory,
            final Function<HttpRequest, HttpServletResponse> delegateResolver) {
        this.factory = factory;
        this.delegateResolver = delegateResolver;
    }

    @Override
    public boolean isContentAvailable(final HttpRequest request) {
        return request.getAttribute(
                SERVLET_RESPONSE_ATTRIBUTE) instanceof ResteasyHttpServletResponse servletResponse
                && servletResponse.isWritten();
    }

    @Override
    public Object inject(final boolean unwrapAsync) {
        return createProxy();
    }

    @Override
    public Object inject(final HttpRequest request, final HttpResponse response, final boolean unwrapAsync) {
        return createProxy();
    }

    private ResteasyHttpServletResponse current() {
        final HttpRequest request = factory.getContextData(HttpRequest.class);
        if (request == null) {
            throw new LoggableFailure(Messages.MESSAGES.unableToFindContextualData(HttpRequest.class.getName()));
        }
        if (request.getAttribute(SERVLET_RESPONSE_ATTRIBUTE) instanceof ResteasyHttpServletResponse servletResponse) {
            return servletResponse;
        }
        final HttpServletResponse delegate = delegateResolver.apply(request);
        if (delegate == null) {
            throw new LoggableFailure(Messages.MESSAGES.unableToFindContextualData(HttpServletResponse.class.getName()));
        }
        final ResteasyHttpServletResponse servletResponse = ResteasyHttpServletResponse.of(delegate);
        request.setAttribute(SERVLET_RESPONSE_ATTRIBUTE, servletResponse);
        return servletResponse;
    }

    private Object createProxy() {
        return Proxy.newProxyInstance(HttpServletResponseValueInjector.class.getClassLoader(),
                new Class<?>[] { HttpServletResponse.class },
                (proxy, method, args) -> {
                    try {
                        return method.invoke(current(), args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
    }
}
