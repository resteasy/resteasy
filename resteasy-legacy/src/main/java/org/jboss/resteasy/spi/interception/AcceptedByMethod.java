package org.jboss.resteasy.spi.interception;

import java.lang.reflect.Method;

/**
 * Implemented interface that can trigger the addition of an interceptor
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy interceptor facility introduced in release 2.x
 * is replaced by the JAX-RS 2.0 compliant interceptor facility in release 3.0.x.
 * 
 * @see <a href="https://jcp.org/en/jsr/detail?id=339">jaxrs-api</a>
 */
@Deprecated
public interface AcceptedByMethod
{
   public boolean accept(Class declaring, Method method);
}
