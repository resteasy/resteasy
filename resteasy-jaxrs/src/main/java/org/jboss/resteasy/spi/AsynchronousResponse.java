package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * This is injected into a jax-rs method via the @Suspend annotation.
 * <p>
 * It allows you to asynchronously send a response in another thread.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 *
 * @deprecated Replaced by javax.ws.rs.container.AsyncResponse
 * 
 * @see javax.ws.rs.container.AsyncResponse
 */
@Deprecated
public interface AsynchronousResponse
{
   void setResponse(Response response);
}
