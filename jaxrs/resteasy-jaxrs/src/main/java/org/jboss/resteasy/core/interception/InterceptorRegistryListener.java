package org.jboss.resteasy.core.interception;

/**
 * Callback interface for when an interceptor registry changes
 *
 * @deprecated
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Deprecated
public interface InterceptorRegistryListener
{
   void registryUpdated(InterceptorRegistry registry);

}
