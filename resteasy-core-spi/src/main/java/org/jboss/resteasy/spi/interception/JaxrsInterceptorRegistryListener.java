package org.jboss.resteasy.spi.interception;

import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry.InterceptorFactory;

/**
 * Callback interface for when an interceptor registry changes
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface JaxrsInterceptorRegistryListener {
    void registryUpdated(JaxrsInterceptorRegistry registry, InterceptorFactory factory);

}
