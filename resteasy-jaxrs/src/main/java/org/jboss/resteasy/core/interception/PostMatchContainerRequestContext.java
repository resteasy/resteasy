package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;

import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PostMatchContainerRequestContext extends PreMatchContainerRequestContext
{
   protected final ResourceMethodInvoker resourceMethod;

   public PostMatchContainerRequestContext(HttpRequest request,ResourceMethodInvoker resourceMethod)
   {
      super(request);
      this.resourceMethod = resourceMethod;
   }

   public ResourceMethodInvoker getResourceMethod()
   {
      return resourceMethod;
   }

   @Override
   public void setMethod(String method)
   {
      throw new IllegalStateException(Messages.MESSAGES.cantSetMethod());
   }

   @Override
   public void setRequestUri(URI requestUri) throws IllegalStateException
   {
      throw new IllegalStateException(Messages.MESSAGES.cantSetURI());
   }

   @Override
   public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException
   {
      throw new IllegalStateException(Messages.MESSAGES.cantSetURI());
   }
}
