package org.jboss.resteasy.plugins.interceptors;

import java.lang.reflect.Method;

import javax.ws.rs.GET;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.FeatureContext;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CacheControlFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext configurable) {
        final Class<?> declaring = resourceInfo.getResourceClass();

        if (declaring == null || resourceInfo.getResourceMethod() == null)
            return;

        final Method method = findInterfaceBasedMethod(declaring, resourceInfo.getResourceMethod());
        if (!method.isAnnotationPresent(GET.class))
            return;

        Cache cache = declaring.getAnnotation(Cache.class);
        NoCache nocache = declaring.getAnnotation(NoCache.class);
        Cache methodCached = method.getAnnotation(Cache.class);
        NoCache noMethodCache = method.getAnnotation(NoCache.class);

        CacheControl cacheControl = null;
        if (methodCached != null) {
            cacheControl = initCacheControl(methodCached);
        } else if (noMethodCache != null) {
            cacheControl = initCacheControl(noMethodCache);
        } else if (cache != null) {
            cacheControl = initCacheControl(cache);
        } else if (nocache != null) {
            cacheControl = initCacheControl(nocache);
        }

        if (cacheControl != null) {
            configurable.register(new CacheControlFilter(cacheControl));
        }
    }

    protected CacheControl initCacheControl(Cache methodCached) {
        CacheControl cacheControl = new CacheControl();
        if (methodCached.isPrivate()) {
            cacheControl.setPrivate(true);
        }
        if (methodCached.maxAge() > -1) {
            cacheControl.setMaxAge(methodCached.maxAge());
        }
        if (methodCached.sMaxAge() > -1) {
            cacheControl.setSMaxAge(methodCached.sMaxAge());
        }
        cacheControl.setMustRevalidate((methodCached.mustRevalidate()));
        cacheControl.setNoStore((methodCached.noStore()));
        cacheControl.setNoTransform((methodCached.noTransform()));
        cacheControl.setProxyRevalidate(methodCached.proxyRevalidate());
        cacheControl.setNoCache(methodCached.noCache());
        return cacheControl;
    }

    protected CacheControl initCacheControl(NoCache value) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoTransform(false);
        for (String field : value.fields())
            cacheControl.getNoCacheFields().add(field);
        return cacheControl;
    }

    private Method findInterfaceBasedMethod(Class<?> root, Method method) {
        if (method.getAnnotation(Cache.class) != null || method.getAnnotation(NoCache.class) != null) {
            return method;
        }

        if (method.getDeclaringClass().isInterface() || root.isInterface()) {
            return method;
        }

        for (Class<?> intf : root.getInterfaces()) {
            try {
                return intf.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException ignored) {
            }
        }

        if (root.getSuperclass() == null || root.getSuperclass().equals(Object.class))
            return method;
        return findInterfaceBasedMethod(root.getSuperclass(), method);
    }
}
