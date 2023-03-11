package org.jboss.resteasy.plugins.interceptors;

import java.lang.reflect.Method;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.FeatureContext;

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
        final Method method = resourceInfo.getResourceMethod();

        if (declaring == null || method == null)
            return;
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
}
