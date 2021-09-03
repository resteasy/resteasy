package org.jboss.resteasy.test.cdi.validation.resource;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;

@Path("async")
public interface AsyncRootResource extends AsyncValidResource
{
   @GET
   @Override
   void getAll(@Suspended AsyncResponse asyncResponse, @BeanParam QueryBeanParamImpl beanParam);

   @Path("/sub")
   AsyncSubResource getSubResource();
}
