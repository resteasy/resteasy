package org.jboss.resteasy.test.cdi.validation.resource;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

public interface SubResource extends ValidResource
{
   @GET
   @Override
   Response getAll(@BeanParam QueryBeanParamImpl beanParam);
}
