package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.InputStream;
import java.net.URI;

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

   @Override
   public void setSecurityContext(SecurityContext context)
   {
      throw new IllegalStateException("Request was already executed");
   }

   @Override
   public void setEntityStream(InputStream entityStream)
   {
      throw new IllegalStateException("Request was already executed");
   }

   @Override
   public void setMethod(String method)
   {
      throw new IllegalStateException("Request was already executed");
   }

   @Override
   public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException
   {
      throw new IllegalStateException("Request was already executed");
   }

   @Override
   public void setRequestUri(URI requestUri) throws IllegalStateException
   {
      throw new IllegalStateException("Request was already executed");
   }
}
