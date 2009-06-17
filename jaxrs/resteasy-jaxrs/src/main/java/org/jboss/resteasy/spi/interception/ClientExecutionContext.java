package org.jboss.resteasy.spi.interception;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ClientExecutionContext
{
   ClientRequest getRequest();

   ClientResponse proceed() throws Exception;
}
