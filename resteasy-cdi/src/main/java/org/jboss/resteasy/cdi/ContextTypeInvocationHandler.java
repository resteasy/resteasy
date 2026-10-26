/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Delegates each invocation to the context object of the request currently being processed. The context object is
 * resolved from the {@link ResteasyContext} on every call, so a proxy can be injected once and used after asynchronous
 * resumption on any thread the request is processed on.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class ContextTypeInvocationHandler implements InvocationHandler {
    private final Class<?> type;

    private ContextTypeInvocationHandler(final Class<?> type) {
        this.type = type;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        Object delegate = factory.getContextData(type);
        // A client invocation pushes its own Providers, otherwise fall back to the server runtime's
        if (delegate == null && Providers.class.isAssignableFrom(type)) {
            delegate = factory;
        }
        // Do not require an active request for the Object methods, e.g. when the proxy is logged
        if (method.getDeclaringClass() == Object.class) {
            return switch (method.getName()) {
                case "equals" -> proxy == args[0];
                case "hashCode" -> System.identityHashCode(proxy);
                case "toString" -> delegate == null ? "Proxy for " + type.getName() : delegate.toString();
                default -> delegate == null ? null : method.invoke(delegate, args);
            };
        }
        if (delegate == null) {
            // No resource method was matched, e.g. a 404, in which case the resource method and class are null
            if (type == ResourceInfo.class) {
                return null;
            }
            throw Messages.MESSAGES.unableToFindContextualData(type.getName());
        }
        try {
            return method.invoke(delegate, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    /**
     * Creates a proxy for the context type which delegates to the context object of the current request.
     *
     * @param type the context type, which must be an interface
     *
     * @return the proxy
     */
    static <T> T createProxy(final Class<T> type) {
        ClassLoader classLoader = type.getClassLoader();
        // The class loader is null if the type was loaded from the bootstrap class loader
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        return type.cast(Proxy.newProxyInstance(classLoader, new Class<?>[] { type },
                new ContextTypeInvocationHandler(type)));
    }
}
