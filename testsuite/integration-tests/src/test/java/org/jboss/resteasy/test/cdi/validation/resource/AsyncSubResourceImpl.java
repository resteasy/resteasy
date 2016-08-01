package org.jboss.resteasy.test.cdi.validation.resource;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

@RequestScoped
public class AsyncSubResourceImpl implements AsyncSubResource
{
   public AsyncSubResourceImpl()
   {
      System.out.println("creating AsyncSubResourceImpl");
   }

   @Override
   public void getAll(AsyncResponse asyncResponse, QueryBeanParamImpl beanParam)
   {
      System.out.println("sub#getAll: beanParam#getParam valid? " + beanParam.getParam());
      asyncResponse.resume(Response.ok().build());
   }
}
