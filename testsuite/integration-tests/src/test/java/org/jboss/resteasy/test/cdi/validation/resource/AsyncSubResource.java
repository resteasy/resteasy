package org.jboss.resteasy.test.cdi.validation.resource;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;

public interface AsyncSubResource extends AsyncValidResource
{
   @GET
   @Override
   void getAll(@Suspended AsyncResponse asyncResponse, @BeanParam QueryBeanParamImpl beanParam);
}
