package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.spi.HttpRequest;

import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PostMatchContainerRequestContext extends PreMatchContainerRequestContext
{
   protected final ResourceMethod resourceMethod;

   public PostMatchContainerRequestContext(HttpRequest request,ResourceMethod resourceMethod)
   {
      super(request);
      this.resourceMethod = resourceMethod;
   }

   public ResourceMethod getResourceMethod()
   {
      return resourceMethod;
   }
}
