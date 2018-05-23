package org.jboss.resteasy.client.core;

import org.jboss.resteasy.client.ClientResponse;

import javax.ws.rs.core.Response;

/**
 * {@link ClientErrorInterceptor} provides a hook into the proxy
 * {@link ClientResponse} request lifecycle. If a Client Proxy method is called,
 * resulting in a client exception, and the proxy return type is not
 * {@link Response} or {@link ClientResponse}, registered interceptors will be
 * given a chance to process the response manually, or throw a new exception. If
 * all interceptors successfully return, RestEasy will re-throw the original
 * encountered exception.
 *
 * @author <a href="mailto:lincoln@ocpsoft.com">Lincoln Baxter, III</a>
 */
public interface ClientErrorInterceptor
{
   /**
    * Attempt to handle the current {@link ClientResponse}. If this method
    * returns successfully, the next registered
    * {@link ClientErrorInterceptor} will attempt to handle the
    * {@link ClientResponse}. If this method throws an exception, no further
    * interceptors will be processed.
    *
    * @param response client response
    * @throws RuntimeException RestEasy will abort request processing if any exception is
    *                          thrown from this method.
    */
   void handle(ClientResponse<?> response) throws RuntimeException;
}
