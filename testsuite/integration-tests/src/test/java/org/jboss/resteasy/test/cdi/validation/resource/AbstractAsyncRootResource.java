package org.jboss.resteasy.test.cdi.validation.resource;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

public abstract class AbstractAsyncRootResource implements AsyncRootResource
{
   @Override
   public void getAll(AsyncResponse asyncResponse, QueryBeanParamImpl beanParam)
   {
      System.out.println("abstract async#getAll: beanParam#getParam valid? " + beanParam.getParam());
      asyncResponse.resume(Response.ok().build());
   }
}
