package org.jboss.resteasy.test.cdi.validation.resource;

import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

@RequestScoped
public class AsyncSubResourceImpl implements AsyncSubResource
{

   private static final Logger LOG = Logger.getLogger(AsyncSubResourceImpl.class);
   public AsyncSubResourceImpl()
   {
      LOG.info("creating AsyncSubResourceImpl");
   }

   @Override
   public void getAll(AsyncResponse asyncResponse, QueryBeanParamImpl beanParam)
   {
      LOG.info("sub#getAll: beanParam#getParam valid? " + beanParam.getParam());
      asyncResponse.resume(Response.ok().build());
   }
}
