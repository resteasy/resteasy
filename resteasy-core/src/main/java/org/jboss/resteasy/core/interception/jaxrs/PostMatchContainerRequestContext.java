package org.jboss.resteasy.core.interception.jaxrs;

import java.net.URI;
import java.util.function.Supplier;

import javax.ws.rs.container.ContainerRequestFilter;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PostMatchContainerRequestContext extends PreMatchContainerRequestContext
{
   protected final ResourceMethodInvoker resourceMethod;

   @Deprecated
   public PostMatchContainerRequestContext(HttpRequest request,ResourceMethodInvoker resourceMethod)
   {
      this(request, resourceMethod, new ContainerRequestFilter[]{}, null);
   }

   public PostMatchContainerRequestContext(HttpRequest request,ResourceMethodInvoker resourceMethod, 
         ContainerRequestFilter[] requestFilters, Supplier<BuiltResponse> continuation)
   {
      super(request, requestFilters, continuation);
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
