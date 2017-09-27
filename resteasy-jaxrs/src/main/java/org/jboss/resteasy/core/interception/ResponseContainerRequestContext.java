package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
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
      super(request, null, null);
   }



   @Override
   public void abortWith(Response response)
   {
      throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
   }

   @Override
   public void setSecurityContext(SecurityContext context)
   {
      throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
   }

   @Override
   public void setEntityStream(InputStream entityStream)
   {
      throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
   }

   @Override
   public void setMethod(String method)
   {
      throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
   }

   @Override
   public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException
   {
      throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
   }

   @Override
   public void setRequestUri(URI requestUri) throws IllegalStateException
   {
      throw new IllegalStateException(Messages.MESSAGES.requestWasAlreadyExecuted());
   }
}
