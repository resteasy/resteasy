package org.jboss.resteasy.client.impl;


import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.InvocationException;
import javax.ws.rs.core.Response;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ClientExecutor
{
   Response invoke(ClientRequest request) throws InvocationException;
   Future<Response> submit(ClientRequest request) throws InvocationException;
   Future<Response> submit(InvocationCallback<Response> callback) throws InvocationException;

   void close() throws Exception;
}
