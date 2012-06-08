package org.jboss.resteasy.client.jaxrs;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ClientHttpEngine
{
   ClientResponse invoke(ClientInvocation request);
   void close();

}
