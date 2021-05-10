package org.jboss.resteasy.test.cdi.validation.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

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
