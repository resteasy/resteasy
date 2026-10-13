/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;

import org.jboss.resteasy.cdi.i18n.LogMessages;

/**
 * Utility for unwrapping CDI client proxies to their underlying contextual instances. Supports both Weld
 * ({@code WeldClientProxy}) and OpenWebBeans ({@code OwbNormalScopeProxy}) via reflective method handles. The proxy
 * interface is resolved from the proxy class itself, not from a class loader, as the proxy class must be able to see
 * the interface it implements. The handles are cached per proxy class, so each deployment resolves its own proxy
 * classes, and the cached handles are released with the proxy class. If the target is not a known proxy,
 * {@link #unwrapIfRequired(Object)} returns the target unchanged.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 * @see CdiProxyUnwrapFilter
 */
class CdiProxyUnwrapper {
    private static final String WELD_PROXY_CLASS = "org.jboss.weld.proxy.WeldClientProxy";
    private static final String OWB_PROXY_CLASS = "org.apache.webbeans.proxy.OwbNormalScopeProxy";
    private static final MethodType UNWRAPPER_TYPE = MethodType.methodType(Object.class, Object.class);
    private static final ClassValue<Optional<MethodHandle>> UNWRAPPERS = new ClassValue<>() {
        @Override
        protected Optional<MethodHandle> computeValue(final Class<?> type) {
            return Optional.ofNullable(resolveUnwrapper(type));
        }
    };

    private CdiProxyUnwrapper() {
    }

    /**
     * Unwraps a CDI proxy to its underlying contextual instance. If the object is not a proxy, it is returned as-is.
     * <p>
     * Getting the contextual instance may create it. An exception thrown while creating it, e.g. a validation failure
     * of the instance, is rethrown. Otherwise, the proxy would create the instance again on the next invocation.
     * </p>
     *
     * @param target the instance
     */
    static Object unwrapIfRequired(final Object target) {
        if (target == null) {
            return null;
        }
        final Optional<MethodHandle> unwrapper = UNWRAPPERS.get(target.getClass());
        if (unwrapper.isEmpty()) {
            // Return original object if it's not a known proxy
            return target;
        }
        try {
            return unwrapper.get().invoke(target);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            // The proxy methods do not declare checked exceptions, so this is not expected
            throw new IllegalStateException(e);
        }
    }

    /**
     * Resolves the handle which returns the contextual instance of a proxy of the given type.
     *
     * @param type the type of the possible proxy
     *
     * @return the handle, or {@code null} if the type is not a known proxy type
     */
    private static MethodHandle resolveUnwrapper(final Class<?> type) {
        final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
        // 1. Weld
        final Class<?> weldProxyClass = findInterface(type, WELD_PROXY_CLASS);
        if (weldProxyClass != null) {
            try {
                final Class<?> metaDataClass = Class.forName(WELD_PROXY_CLASS + "$Metadata", false,
                        weldProxyClass.getClassLoader());
                final MethodHandle metaDataGetter = lookup.findVirtual(weldProxyClass, "getMetadata",
                        MethodType.methodType(metaDataClass));
                final MethodHandle instanceGetter = lookup.findVirtual(metaDataClass, "getContextualInstance",
                        MethodType.methodType(Object.class));
                return MethodHandles.filterReturnValue(metaDataGetter, instanceGetter).asType(UNWRAPPER_TYPE);
            } catch (ReflectiveOperationException e) {
                LogMessages.LOGGER.debugf(e,
                        "Unable to resolve the Weld proxy methods for %s. The proxy will not be unwrapped.",
                        type.getName());
                return null;
            }
        }
        // 2. OpenWebBeans
        final Class<?> owbProxyClass = findInterface(type, OWB_PROXY_CLASS);
        if (owbProxyClass != null) {
            try {
                return lookup.findVirtual(owbProxyClass, "Owb_getSystemInstance", MethodType.methodType(Object.class))
                        .asType(UNWRAPPER_TYPE);
            } catch (ReflectiveOperationException e) {
                LogMessages.LOGGER.debugf(e,
                        "Unable to resolve the OpenWebBeans proxy methods for %s. The proxy will not be unwrapped.",
                        type.getName());
                return null;
            }
        }
        return null;
    }

    /**
     * Finds the interface with the given name implemented by the type, its super classes or their interfaces.
     *
     * @param type the type to search
     * @param name the name of the interface
     *
     * @return the interface, or {@code null} if the type does not implement it
     */
    private static Class<?> findInterface(final Class<?> type, final String name) {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            for (Class<?> intf : current.getInterfaces()) {
                final Class<?> found = findInterfaceInHierarchy(intf, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static Class<?> findInterfaceInHierarchy(final Class<?> intf, final String name) {
        if (intf.getName().equals(name)) {
            return intf;
        }
        for (Class<?> superIntf : intf.getInterfaces()) {
            final Class<?> found = findInterfaceInHierarchy(superIntf, name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
