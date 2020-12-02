package org.jboss.resteasy.test.cdi.validation.resource;

import jakarta.ws.rs.QueryParam;

public class QueryBeanParamImpl implements QueryBeanParam
{
   @QueryParam("foo")
   private String param;

   @Override
   public String getParam()
   {
      return param;
   }
}
