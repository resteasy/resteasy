package org.jboss.resteasy.core.interception;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MessageResponseInterceptorFactory
{
   /**
    * @param method jax-rs method to be intercepted
    * @return null if no interceptor should be bound to this method
    */
   MessageResponseInterceptor create(Method method);
}
