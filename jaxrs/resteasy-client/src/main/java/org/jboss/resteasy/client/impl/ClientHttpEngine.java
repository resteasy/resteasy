package org.jboss.resteasy.client.impl;


import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.InvocationException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ClientHttpEngine
{
   ClientResponse invoke(ClientInvocation request) throws InvocationException;
   void close() throws Exception;
}
