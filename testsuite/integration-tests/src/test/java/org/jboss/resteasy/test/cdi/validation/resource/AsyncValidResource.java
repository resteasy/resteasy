package org.jboss.resteasy.test.cdi.validation.resource;

import jakarta.validation.Valid;
import jakarta.ws.rs.container.AsyncResponse;

public interface AsyncValidResource
{
   void getAll(AsyncResponse asyncResponse, @Valid QueryBeanParamImpl beanParam);
}
