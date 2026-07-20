/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.jboss.resteasy.cdi.i18n.LogMessages;

/**
 * Utility for unwrapping CDI client proxies to their underlying contextual instances. Supports both Weld
 * ({@code WeldClientProxy}) and OpenWebBeans ({@code OwbNormalScopeProxy}) via reflective method handles that are
 * resolved once at class-load time. If neither implementation is on the classpath, {@link #unwrapIfRequired(Object)}
 * returns the target unchanged.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 * @see CdiProxyUnwrapFilter
 */
class CdiProxyUnwrapper {

    // Weld Handles
    private static final Class<?> WELD_PROXY_CLASS;
    private static final MethodHandle WELD_METADATA_GETTER;
    private static final MethodHandle WELD_CONTEXTUAL_INSTANCE_GETTER;

    // OpenWebBeans Handles
    private static final Class<?> OWB_PROXY_CLASS;
    private static final MethodHandle OWB_INSTANCE_GETTER;

    static {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final MethodHandles.Lookup lookup = MethodHandles.publicLookup();

        // 1. Initialize Weld
        Class<?> weldProxyClass = null;
        MethodHandle weldMetaGetter = null;
        MethodHandle weldInstGetter = null;
        try {
            weldProxyClass = Class.forName("org.jboss.weld.proxy.WeldClientProxy", false, tccl);
            final Class<?> metaDataClass = Class.forName("org.jboss.weld.proxy.WeldClientProxy$Metadata", false, tccl);
            weldMetaGetter = lookup.findVirtual(weldProxyClass, "getMetadata", MethodType.methodType(metaDataClass));
            weldInstGetter = lookup.findVirtual(metaDataClass, "getContextualInstance", MethodType.methodType(Object.class));
        } catch (Throwable e) {
            // Weld is not on the classpath
            LogMessages.LOGGER.trace(
                    "Unable to find Weld proxy class. Weld may not be on the class path. Weld proxies will not be unwrapped.",
                    e);
        }
        WELD_PROXY_CLASS = weldProxyClass;
        WELD_METADATA_GETTER = weldMetaGetter;
        WELD_CONTEXTUAL_INSTANCE_GETTER = weldInstGetter;

        // 2. Initialize OpenWebBeans
        Class<?> owbProxyClass = null;
        MethodHandle owbInstGetter = null;
        try {
            owbProxyClass = Class.forName("org.apache.webbeans.proxy.OwbNormalScopeProxy", false, tccl);
            owbInstGetter = lookup.findVirtual(owbProxyClass, "Owb_getSystemInstance", MethodType.methodType(Object.class));
        } catch (Throwable e) {
            // OpenWebBeans is not on the classpath
            LogMessages.LOGGER.trace(
                    "Unable to find OpenWebBeans proxy class. OpenWebBeans may not be on the class path. OpenWebBeans proxies will not be unwrapped.",
                    e);
        }
        OWB_PROXY_CLASS = owbProxyClass;
        OWB_INSTANCE_GETTER = owbInstGetter;
    }

    private CdiProxyUnwrapper() {
    }

    /**
     * Unwraps a CDI proxy to its underlying contextual instance. If the object is not a proxy, it is returned as-is.
     *
     * @param target the instance
     */
    static Object unwrapIfRequired(final Object target) {
        if (target == null) {
            return null;
        }

        try {
            // Check Weld
            if (WELD_PROXY_CLASS != null && WELD_PROXY_CLASS.isInstance(target)) {
                Object metaData = WELD_METADATA_GETTER.invoke(target);
                return WELD_CONTEXTUAL_INSTANCE_GETTER.invoke(metaData);
            }

            // Check OpenWebBeans
            if (OWB_PROXY_CLASS != null && OWB_PROXY_CLASS.isInstance(target)) {
                return OWB_INSTANCE_GETTER.invoke(target);
            }
        } catch (Throwable e) {
            LogMessages.LOGGER.debugf(e, "Failed to unwrap potential CDI proxy: %s", target);
        }

        // Return original object if it's not a known proxy or unwrapping failed
        return target;
    }
}
