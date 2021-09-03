package org.jboss.resteasy.test.cdi.validation.resource;

import jakarta.validation.constraints.Size;

public interface QueryBeanParam
{
   @Size(min = 2)
   String getParam();
}
