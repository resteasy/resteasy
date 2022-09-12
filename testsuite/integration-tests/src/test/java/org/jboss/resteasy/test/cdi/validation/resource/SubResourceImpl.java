package org.jboss.resteasy.test.cdi.validation.resource;

import org.jboss.logging.Logger;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class SubResourceImpl implements SubResource
{
   static boolean methodEntered;
   private static final Logger LOG = Logger.getLogger(SubResourceImpl.class);

   @Override
   public Response getAll(QueryBeanParamImpl beanParam)
   {
      LOG.info("beanParam#getParam valid? " + beanParam.getParam());
      methodEntered = true;
      return Response.ok().build();
   }
}
