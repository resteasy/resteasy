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
 * 
 * @deprecated The Resteasy client framework in resteasy-jaxrs
 *             is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 *             
 *             The Resteasy interceptor facility introduced in release 2.x
 *             is replaced by the JAX-RS 2.0 compliant interceptor facility in release 3.0.x.
 * 
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */
@Deprecated
public interface ClientErrorInterceptor
{
   /**
    * Attempt to handle the current {@link ClientResponse}. If this method
    * returns successfully, the next registered
    * {@link ClientErrorInterceptor} will attempt to handle the
    * {@link ClientResponse}. If this method throws an exception, no further
    * interceptors will be processed.
    *
    * @throws RuntimeException RestEasy will abort request processing if any exception is
    *                          thrown from this method.
    */
   void handle(ClientResponse<?> response) throws RuntimeException;
}
