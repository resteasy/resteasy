package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResponseContainerRequestContext extends PreMatchContainerRequestContext
{
   public ResponseContainerRequestContext(HttpRequest request)
   {
      super(request);
   }

   @Override
   public void abortWith(Response response)
   {
      throw new IllegalStateException("Request was already executed");
   }
}
