package org.jboss.resteasy.test.cdi.validation.resource;

import javax.validation.Valid;
import javax.ws.rs.container.AsyncResponse;

public interface AsyncValidResource
{
   void getAll(AsyncResponse asyncResponse, @Valid QueryBeanParamImpl beanParam);
}
