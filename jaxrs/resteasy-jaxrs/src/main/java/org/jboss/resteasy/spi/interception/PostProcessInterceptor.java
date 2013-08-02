package org.jboss.resteasy.spi.interception;

import org.jboss.resteasy.core.ServerResponse;

/**
 * Invoked in order, gives you access to the response before MessageBodyReader and Writers get invoked.
 *
 * @deprecated
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Deprecated
public interface PostProcessInterceptor
{
   void postProcess(ServerResponse response);
}
