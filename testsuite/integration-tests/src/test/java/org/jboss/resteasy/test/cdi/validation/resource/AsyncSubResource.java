package org.jboss.resteasy.test.cdi.validation.resource;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

public interface AsyncSubResource extends AsyncValidResource
{
   @GET
   @Override
   void getAll(@Suspended AsyncResponse asyncResponse, @BeanParam QueryBeanParamImpl beanParam);
}
