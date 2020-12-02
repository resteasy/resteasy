package org.jboss.resteasy.test.cdi.validation.resource;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Response;

public interface SubResource extends ValidResource
{
   @GET
   @Override
   Response getAll(@BeanParam QueryBeanParamImpl beanParam);
}
