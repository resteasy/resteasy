package org.jboss.resteasy.test.cdi.validation.resource;

import javax.validation.Valid;
import javax.ws.rs.core.Response;

public interface ValidResource
{
   Response getAll(@Valid QueryBeanParamImpl beanParam);
}
