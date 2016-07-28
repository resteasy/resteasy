package org.jboss.resteasy.test.cdi.validation.resource;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class AsyncRootResourceImpl extends AbstractAsyncRootResource
{
   @Inject
   private AsyncSubResourceImpl subResource;

   @Override
   public AsyncSubResource getSubResource()
   {
      return subResource;
   }
}
