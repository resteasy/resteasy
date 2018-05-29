package org.jboss.resteasy.spi.interception;

import org.jboss.resteasy.core.ServerResponse;

/**
 * Invoked in order, gives you access to the response before MessageBodyReader and Writers get invoked.
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
public interface PostProcessInterceptor
{
   void postProcess(ServerResponse response);
}
