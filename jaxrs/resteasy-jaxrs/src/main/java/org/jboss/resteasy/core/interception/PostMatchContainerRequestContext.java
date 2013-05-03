package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.spi.HttpRequest;

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
}
