package org.jboss.resteasy.test.cdi.validation.resource;

import javax.ws.rs.QueryParam;

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
