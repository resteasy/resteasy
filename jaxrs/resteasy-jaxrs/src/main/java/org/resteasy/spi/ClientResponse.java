package org.jboss.resteasy.spi;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Response interface for the RESTEasy client framework.  Use this in your client proxy interface method return type
 * declarations if you want access to the response entity as well as status and header information.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ClientResponse<T>
{
   T getEntity();

   MultivaluedMap<String, String> getHeaders();

   int getStatus();
}
